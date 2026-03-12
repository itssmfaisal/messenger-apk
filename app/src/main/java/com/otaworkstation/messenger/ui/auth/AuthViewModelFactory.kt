package com.otaworkstation.messenger.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.otaworkstation.messenger.data.repository.MessengerRepository
import com.otaworkstation.messenger.util.TokenManager

class AuthViewModelFactory(
    private val repository: MessengerRepository,
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository, tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}