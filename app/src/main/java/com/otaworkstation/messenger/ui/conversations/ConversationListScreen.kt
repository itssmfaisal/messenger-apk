package com.otaworkstation.messenger.ui.conversations

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
// ...existing code...
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.otaworkstation.messenger.util.UrlUtils
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import android.widget.Toast
import com.otaworkstation.messenger.util.checkUrlStatus
import com.otaworkstation.messenger.util.rememberAuthImageLoader
import com.otaworkstation.messenger.data.model.ConversationDTO
import com.otaworkstation.messenger.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationListScreen(
    viewModel: ConversationsViewModel,
    onConversationClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit
) {
    val conversations by viewModel.conversations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadConversations()
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(White)) {
                CenterAlignedTopAppBar(
                    title = { Text("Chat", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                    navigationIcon = {
                        IconButton(onClick = { /* TODO */ }) {
                            Icon(Icons.Default.ArrowBackIosNew, contentDescription = null, modifier = Modifier.size(20.dp))
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* TODO */ }) {
                            Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(20.dp))
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = White)
                )
                
                // Profile Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.size(80.dp)) {
                        Surface(
                            modifier = Modifier.size(80.dp).clickable { onProfileClick() },
                            shape = CircleShape,
                            color = BorderGray
                        ) {
                            // Placeholder for user avatar
                        }
                        // Online indicator
                        Surface(
                            modifier = Modifier.size(14.dp).align(Alignment.BottomEnd).offset(x = (-4).dp, y = (-4).dp),
                            shape = CircleShape,
                            color = PrimaryGreen,
                            border = BorderStroke(2.dp, White)
                        ) {}
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "faisal", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = BackgroundLightGreen,
                        modifier = Modifier.clickable { /* TODO */ }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(modifier = Modifier.size(6.dp), shape = CircleShape, color = PrimaryGreen) {}
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("available", color = PrimaryGreen, fontSize = 12.sp)
                            Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(12.dp).padding(start = 4.dp), tint = PrimaryGreen)
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar {
                var selectedIndex by remember { mutableStateOf(0) }

                NavigationBarItem(
                    selected = selectedIndex == 0,
                    onClick = { selectedIndex = 0 },
                    icon = { Icon(Icons.Default.ChatBubble, contentDescription = "Chats") },
                    label = { Text("Chats") }
                )

                NavigationBarItem(
                    selected = selectedIndex == 1,
                    onClick = { selectedIndex = 1 },
                    icon = { Icon(Icons.Default.PhotoLibrary, contentDescription = "Stories") },
                    label = { Text("Stories") }
                )

                NavigationBarItem(
                    selected = selectedIndex == 2,
                    onClick = { selectedIndex = 2 },
                    icon = { Icon(Icons.Default.Notifications, contentDescription = "Notifications") },
                    label = { Text("Notifications") }
                )

                NavigationBarItem(
                    selected = selectedIndex == 3,
                    onClick = { selectedIndex = 3 },
                    icon = { Icon(Icons.Default.Menu, contentDescription = "Menu") },
                    label = { Text("Menu") }
                )
            }
        },
        floatingActionButton = {
            Surface(
                shape = RoundedCornerShape(28.dp),
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = White,
                modifier = Modifier
                    .padding(end = 16.dp, bottom = 72.dp)
                    .clickable { /* TODO: open meta ai */ }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape,
                        color = PrimaryGreen
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = White, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Ask Meta AI", color = TextDark, fontWeight = FontWeight.SemiBold)
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(White)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search", color = TextGray.copy(alpha = 0.5f)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextGray) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF3F4F6),
                    focusedContainerColor = Color(0xFFF3F4F6),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                ),
                singleLine = true
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Last chats", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                Row {
                    Icon(Icons.Default.Add, contentDescription = null, tint = TextGray, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(Icons.Default.MoreHoriz, contentDescription = null, tint = TextGray, modifier = Modifier.size(20.dp))
                }
            }

            if (isLoading && conversations.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryGreen)
                }
            } else {
                LazyColumn {
                    items(conversations) { conversation ->
                        ConversationItem(
                            conversation,
                            onClick = { onConversationClick(conversation.partner) },
                            onProfileClick = { onProfileClick() }
                        )
                    }
                    item {
                        if (viewModel.canLoadMore()) {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Button(onClick = { viewModel.loadNextPage() }) {
                                    Text("Load More")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConversationItem(
    conversation: ConversationDTO,
    onClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = BorderGray
        ) {
            // If the conversation provides a partnerProfilePictureUrl, load it with Coil; otherwise show placeholder
            val context = LocalContext.current
            val imageUrl = UrlUtils.resolveMediaUrl(conversation.partnerProfilePictureUrl)
            val authLoader = rememberAuthImageLoader()
            val scope = rememberCoroutineScope()
            if (!imageUrl.isNullOrBlank()) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context).data(imageUrl).crossfade(true).build(),
                    imageLoader = authLoader,
                    contentDescription = "Avatar for ${conversation.partner}",
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { onProfileClick() },
                                onLongPress = {
                                    scope.launch {
                                        val url = imageUrl
                                        val status = checkUrlStatus(context, url ?: "")
                                        val message = if (status.first != null) "URL: $url — HTTP ${status.first}" else "URL: $url — Error: ${status.second}"
                                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                    }
                                }
                            )
                        },
                    contentScale = ContentScale.Crop
                ) {
                    val state = painter.state
                    if (state is coil.compose.AsyncImagePainter.State.Loading) {
                        Box(modifier = Modifier.fillMaxSize().background(Color.LightGray)) {}
                    } else if (state is coil.compose.AsyncImagePainter.State.Error) {
                        Box(modifier = Modifier.fillMaxSize().background(Color.Gray), contentAlignment = Alignment.Center) {
                            Text(text = conversation.partner.take(1).uppercase(), color = Color.White)
                        }
                    } else {
                        SubcomposeAsyncImageContent()
                    }
                }
            } else {
                // Empty placeholder
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = conversation.partner.take(1).uppercase(), color = Color.White)
                }
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = conversation.partner,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextDark
            )
        }
        Text(
            text = conversation.lastMessageAt.takeLast(5), // Simplified timestamp
            fontSize = 12.sp,
            color = TextGray
        )
    }
}