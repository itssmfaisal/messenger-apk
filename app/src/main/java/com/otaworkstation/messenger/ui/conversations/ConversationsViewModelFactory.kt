package com.otaworkstation.messenger.ui.conversations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.otaworkstation.messenger.data.repository.MessengerRepository

class ConversationsViewModelFactory(
    private val repository: MessengerRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConversationsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ConversationsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}