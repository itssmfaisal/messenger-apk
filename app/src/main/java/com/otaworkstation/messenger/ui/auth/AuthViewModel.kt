package com.otaworkstation.messenger.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otaworkstation.messenger.data.model.AuthRequest
import com.otaworkstation.messenger.data.repository.MessengerRepository
import com.otaworkstation.messenger.util.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: MessengerRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = repository.login(AuthRequest(username, password))
                if (response.isSuccessful) {
                    val token = response.body()?.token
                    if (token != null) {
                        tokenManager.saveToken(token)
                        tokenManager.saveUsername(username)
                        _authState.value = AuthState.Success
                    } else {
                        _authState.value = AuthState.Error("Token is null")
                    }
                } else {
                    _authState.value = AuthState.Error("Login failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = repository.register(AuthRequest(username, password, email))
                if (response.isSuccessful) {
                    // After registration, user can login
                    _authState.value = AuthState.RegisterSuccess
                } else {
                    _authState.value = AuthState.Error("Registration failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    object RegisterSuccess : AuthState()
    data class Error(val message: String) : AuthState()
}