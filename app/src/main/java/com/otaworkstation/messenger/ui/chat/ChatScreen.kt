package com.otaworkstation.messenger.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.otaworkstation.messenger.data.model.Message
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.otaworkstation.messenger.util.UrlUtils
import androidx.compose.ui.layout.ContentScale
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import com.otaworkstation.messenger.util.rememberAuthImageLoader
import com.otaworkstation.messenger.data.model.MessageStatus
import com.otaworkstation.messenger.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    partnerUsername: String,
    onBackClick: () -> Unit
) {
    val messages by viewModel.messages.collectAsState()
    var textState by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var viewerImageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(White)) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = CircleShape,
                                color = BorderGray
                            ) {}
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(partnerUsername, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(modifier = Modifier.size(6.dp), shape = CircleShape, color = Color.Gray) {}
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Offline", fontSize = 12.sp, color = TextGray)
                                }
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
                )
                
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth()
                ) {
                    TabItem(text = "Messages", isSelected = selectedTab == 0) { selectedTab = 0 }
                    Spacer(modifier = Modifier.width(8.dp))
                    TabItem(text = "Media", isSelected = selectedTab == 1) { selectedTab = 1 }
                }
            }
        },
        bottomBar = {
            Surface(tonalElevation = 2.dp, color = White) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = textState,
                        onValueChange = { textState = it },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp),
                        placeholder = { Text("Write your message...", color = TextGray.copy(alpha = 0.5f)) },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = BorderGray,
                            focusedBorderColor = PrimaryGreen,
                            unfocusedContainerColor = BackgroundLightGreen.copy(alpha = 0.3f),
                            focusedContainerColor = BackgroundLightGreen.copy(alpha = 0.3f)
                        ),
                        trailingIcon = {
                            IconButton(onClick = { /* TODO */ }) {
                                Icon(Icons.Default.AttachFile, contentDescription = null, tint = TextGray)
                            }
                        },
                        maxLines = 4
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = PrimaryGreen,
                        enabled = textState.isNotBlank(),
                        onClick = {
                            viewModel.sendMessage(textState)
                            textState = ""
                        }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = White, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(White)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(messages) { message ->
                MessageBubble(message, isMe = message.sender != partnerUsername, onImageClick = { url ->
                    viewerImageUrl = url
                })
            }
        }
    }

    // Full screen image viewer dialog
    if (!viewerImageUrl.isNullOrBlank()) {
        Dialog(onDismissRequest = { viewerImageUrl = null }) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                val ctx = LocalContext.current
                val authLoader = rememberAuthImageLoader()
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(ctx).data(viewerImageUrl).crossfade(true).build(),
                    imageLoader = authLoader,
                    contentDescription = "Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                ) {
                    val state = painter.state
                    when (state) {
                        is coil.compose.AsyncImagePainter.State.Loading -> {
                            Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {}
                        }
                        is coil.compose.AsyncImagePainter.State.Error -> {
                            Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Couldn't load image", color = Color.White)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(onClick = { /* retry by resetting viewer; simple refresh */ }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)) {
                                        Text("Close", color = Color.White)
                                    }
                                }
                            }
                        }
                        else -> {
                            SubcomposeAsyncImageContent()
                        }
                    }
                }
                IconButton(onClick = { viewerImageUrl = null }, modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun TabItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) PrimaryGreen else Color.Transparent
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            color = if (isSelected) White else TextGray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}

@Composable
fun MessageBubble(message: Message, isMe: Boolean, onImageClick: (String) -> Unit) {
    val alignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleColor = if (isMe) BubbleOutgoing else BubbleIncoming
    val shape = if (isMe) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 2.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 2.dp, bottomEnd = 16.dp)
    }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = if (isMe) Alignment.End else Alignment.Start) {
        Row(verticalAlignment = Alignment.Bottom) {
            if (!isMe) {
                Surface(modifier = Modifier.size(32.dp), shape = CircleShape, color = BorderGray) {}
                Spacer(modifier = Modifier.width(8.dp))
            }
            
                Column(horizontalAlignment = if (isMe) Alignment.End else Alignment.Start) {
                if (!isMe) {
                    Text(message.sender, fontSize = 12.sp, color = TextDark, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
                }
                Surface(
                    color = bubbleColor,
                    shape = shape
                ) {
                    // If the message carries an attachmentUrl, handle it by type
                    val ctx = LocalContext.current
                    val resolved = UrlUtils.resolveMediaUrl(message.attachmentUrl)
                    val attachmentType = message.attachmentType?.lowercase()

                    if (!resolved.isNullOrBlank()) {
                        when {
                            // Image attachments: show inline preview and allow full-screen tap
                            attachmentType?.startsWith("image") == true || resolved.matches(Regex(".*\\.(png|jpg|jpeg|webp|gif)(\\?.*)?", RegexOption.IGNORE_CASE)) -> {
                                val authLoader = rememberAuthImageLoader()
                                SubcomposeAsyncImage(
                                    model = ImageRequest.Builder(ctx).data(resolved).crossfade(true).build(),
                                    imageLoader = authLoader,
                                    contentDescription = message.attachmentName ?: "image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 240.dp)
                                        .padding(8.dp)
                                        .clickable { onImageClick(resolved) },
                                    contentScale = ContentScale.Crop
                                ) {
                                    val state = painter.state
                                    if (state is coil.compose.AsyncImagePainter.State.Loading) {
                                        Box(modifier = Modifier.fillMaxSize().background(Color.LightGray)) {}
                                    } else if (state is coil.compose.AsyncImagePainter.State.Error) {
                                        Box(modifier = Modifier.fillMaxSize().background(Color.Gray)) {}
                                    } else {
                                        SubcomposeAsyncImageContent()
                                    }
                                }
                            }

                            // Video attachments: show a tappable thumbnail (here: use AsyncImage for thumbnail if URL points to an image), open external intent on click
                            attachmentType?.startsWith("video") == true || resolved.matches(Regex(".*\\.(mp4|mov|webm|mkv)(\\?.*)?", RegexOption.IGNORE_CASE)) -> {
                                // Use AsyncImage to show a thumbnail if available, otherwise show a Play icon
                                Box(modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 240.dp)
                                    .padding(8.dp)
                                    .clickable {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(resolved))
                                        intent.setDataAndType(Uri.parse(resolved), "video/*")
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        ctx.startActivity(intent)
                                    }
                                ) {
                                    val authLoader = rememberAuthImageLoader()
                                    SubcomposeAsyncImage(
                                        model = ImageRequest.Builder(ctx).data(resolved).crossfade(true).build(),
                                        imageLoader = authLoader,
                                        contentDescription = message.attachmentName ?: "video",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    ) {
                                        val state = painter.state
                                        if (state is coil.compose.AsyncImagePainter.State.Loading) {
                                            Box(modifier = Modifier.fillMaxSize().background(Color.LightGray)) {}
                                        } else if (state is coil.compose.AsyncImagePainter.State.Error) {
                                            Box(modifier = Modifier.fillMaxSize().background(Color.Gray)) {}
                                        } else {
                                            SubcomposeAsyncImageContent()
                                        }
                                    }
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play", modifier = Modifier.align(Alignment.Center), tint = Color.White)
                                }
                            }

                            // PDF or other docs: show an icon+name and open external viewer
                            attachmentType?.contains("pdf") == true || resolved.matches(Regex(".*\\.pdf(\\?.*)?", RegexOption.IGNORE_CASE)) -> {
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(resolved))
                                        intent.setDataAndType(Uri.parse(resolved), "application/pdf")
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        ctx.startActivity(intent)
                                    }
                                    .padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AttachFile, contentDescription = "pdf", tint = TextGray)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(message.attachmentName ?: "Document", color = TextDark)
                                }
                            }

                            else -> {
                                // Fallback: show message content
                                Text(
                                    text = message.content,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                    color = TextDark,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    } else {
                        Text(
                            text = message.content,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            color = TextDark,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier.padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message.sentAt.takeLast(5), // Simple time format
                fontSize = 11.sp,
                color = TextGray
            )
            if (isMe) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = if (message.status == MessageStatus.SENT) Icons.Default.Check else Icons.Default.DoneAll,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = if (message.status == MessageStatus.SEEN) PrimaryGreen else TextGray
                )
            }
        }
    }
}