package com.otaworkstation.messenger.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.otaworkstation.messenger.data.repository.MessengerRepository
import com.otaworkstation.messenger.util.TokenManager

class ChatViewModelFactory(
    private val repository: MessengerRepository,
    private val tokenManager: TokenManager,
    private val partnerUsername: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(repository, tokenManager, partnerUsername) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}