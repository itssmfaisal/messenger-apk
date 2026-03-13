package com.otaworkstation.messenger.ui.profile
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.otaworkstation.messenger.util.TokenManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    tokenManager: TokenManager,
    onBackClick: () -> Unit,
    onLogout: () -> Unit
) {
    val profile by viewModel.profile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var displayName by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }

    LaunchedEffect(profile) {
        profile?.let {
            displayName = it.displayName ?: ""
            bio = it.bio ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading && profile == null) {
                CircularProgressIndicator()
            } else {
                    // Profile picture
                    val imageUrl = com.otaworkstation.messenger.util.UrlUtils.resolveMediaUrl(profile?.profilePictureUrl)
                    val authLoader = com.otaworkstation.messenger.util.rememberAuthImageLoader()
                    android.util.Log.d("ProfileScreen", "profilePictureUrl: ${profile?.profilePictureUrl}")
                    android.util.Log.d("ProfileScreen", "resolved imageUrl: $imageUrl")
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        if (!imageUrl.isNullOrBlank()) {
                            SubcomposeAsyncImage(
                                model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true).build(),
                                imageLoader = authLoader,
                                contentDescription = "Profile picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            ) {
                                val state = painter.state
                                if (state is coil.compose.AsyncImagePainter.State.Loading) {
                                    Box(modifier = Modifier.fillMaxSize().background(Color.LightGray)) {}
                                } else if (state is coil.compose.AsyncImagePainter.State.Error) {
                                    Box(modifier = Modifier.fillMaxSize().background(Color.Gray), contentAlignment = Alignment.Center) {
                                        Text(text = (profile?.displayName?.take(1)?.uppercase() ?: "?"), color = Color.White)
                                    }
                                } else {
                                    SubcomposeAsyncImageContent()
                                }
                            }
                        } else {
                            Box(modifier = Modifier.fillMaxSize().background(Color.Gray), contentAlignment = Alignment.Center) {
                                Text(text = (profile?.displayName?.take(1)?.uppercase() ?: "?"), color = Color.White)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Display Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.updateProfile(displayName, bio) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Update Profile")
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        tokenManager.clearToken()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Logout")
                }
            }
        }
    }
}