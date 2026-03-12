package com.otaworkstation.messenger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.otaworkstation.messenger.data.remote.NetworkModule
import com.otaworkstation.messenger.data.repository.MessengerRepository
import com.otaworkstation.messenger.ui.auth.AuthScreen
import com.otaworkstation.messenger.ui.auth.AuthViewModel
import com.otaworkstation.messenger.ui.auth.AuthViewModelFactory
import com.otaworkstation.messenger.ui.chat.ChatScreen
import com.otaworkstation.messenger.ui.chat.ChatViewModel
import com.otaworkstation.messenger.ui.chat.ChatViewModelFactory
import com.otaworkstation.messenger.ui.conversations.ConversationListScreen
import com.otaworkstation.messenger.ui.conversations.ConversationsViewModel
import com.otaworkstation.messenger.ui.conversations.ConversationsViewModelFactory
import com.otaworkstation.messenger.ui.profile.ProfileScreen
import com.otaworkstation.messenger.ui.profile.ProfileViewModel
import com.otaworkstation.messenger.ui.profile.ProfileViewModelFactory
import com.otaworkstation.messenger.ui.theme.MessengerTheme
import com.otaworkstation.messenger.util.TokenManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val tokenManager = TokenManager(this)
        val networkModule = NetworkModule(tokenManager)
        val repository = MessengerRepository(networkModule.api)
        val authViewModelFactory = AuthViewModelFactory(repository, tokenManager)
        val conversationsViewModelFactory = ConversationsViewModelFactory(repository)
        val profileViewModelFactory = ProfileViewModelFactory(repository)

        setContent {
            MessengerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MessengerApp(
                        authViewModelFactory,
                        conversationsViewModelFactory,
                        profileViewModelFactory,
                        repository,
                        tokenManager
                    )
                }
            }
        }
    }
}

@Composable
fun MessengerApp(
    authViewModelFactory: AuthViewModelFactory,
    conversationsViewModelFactory: ConversationsViewModelFactory,
    profileViewModelFactory: ProfileViewModelFactory,
    repository: MessengerRepository,
    tokenManager: TokenManager
) {
    val navController = rememberNavController()
    val startDestination = if (tokenManager.getToken() != null) "conversations" else "auth"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("auth") {
            val viewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
            AuthScreen(
                viewModel = viewModel,
                onAuthSuccess = {
                    navController.navigate("conversations") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }
        composable("conversations") {
            val viewModel: ConversationsViewModel = viewModel(factory = conversationsViewModelFactory)
            ConversationListScreen(
                viewModel = viewModel,
                onConversationClick = { username ->
                    navController.navigate("chat/$username")
                },
                onProfileClick = {
                    navController.navigate("profile")
                }
            )
        }
        composable(
            "chat/{username}",
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            val chatViewModelFactory = ChatViewModelFactory(repository, tokenManager, username)
            val viewModel: ChatViewModel = viewModel(factory = chatViewModelFactory)
            ChatScreen(
                viewModel = viewModel,
                partnerUsername = username,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable("profile") {
            val viewModel: ProfileViewModel = viewModel(factory = profileViewModelFactory)
            ProfileScreen(
                viewModel = viewModel,
                tokenManager = tokenManager,
                onBackClick = {
                    navController.popBackStack()
                },
                onLogout = {
                    navController.navigate("auth") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}