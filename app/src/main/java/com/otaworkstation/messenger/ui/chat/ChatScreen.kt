package com.otaworkstation.messenger.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.otaworkstation.messenger.data.model.Message
import com.otaworkstation.messenger.data.model.MessageStatus

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

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(partnerUsername) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 2.dp) {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = textState,
                        onValueChange = { textState = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Message") },
                        maxLines = 4
                    )
                    IconButton(
                        onClick = {
                            viewModel.sendMessage(textState)
                            textState = ""
                        },
                        enabled = textState.isNotBlank()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
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
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(messages) { message ->
                MessageBubble(message, isMe = message.sender != partnerUsername)
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message, isMe: Boolean) {
    val alignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
    val color = if (isMe) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
    val shape = if (isMe) {
        RoundedCornerShape(16.dp, 16.dp, 2.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 2.dp)
    }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Column(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(color, shape)
                .padding(12.dp)
        ) {
            Text(text = message.content)
            Row(
                modifier = Modifier.align(Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = message.sentAt.takeLast(8), // Just showing time part for simplicity
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (isMe) {
                    Spacer(modifier = Modifier.width(4.dp))
                    val statusText = when (message.status) {
                        MessageStatus.SENT -> "✓"
                        MessageStatus.DELIVERED -> "✓✓"
                        MessageStatus.SEEN -> "✓✓"
                    }
                    val statusColor = if (message.status == MessageStatus.SEEN) Color.Blue else Color.Gray
                    Text(
                        text = statusText,
                        fontSize = 10.sp,
                        color = statusColor
                    )
                }
            }
        }
    }
}