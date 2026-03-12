package com.otaworkstation.messenger.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otaworkstation.messenger.data.model.Message
import com.otaworkstation.messenger.data.repository.MessengerRepository
import com.otaworkstation.messenger.data.remote.WebSocketManager
import com.otaworkstation.messenger.util.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repository: MessengerRepository,
    private val tokenManager: TokenManager,
    private val partnerUsername: String
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var webSocketManager: WebSocketManager? = null

    init {
        loadHistory()
        setupWebSocket()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getConversationHistory(partnerUsername, 0, 50)
                if (response.isSuccessful) {
                    val history = response.body()?.content ?: emptyList()
                    // History is newest first, reverse it for chronological display
                    _messages.value = history.reversed()
                    
                    // Mark last received message as seen if it's from partner
                    history.firstOrNull { it.sender == partnerUsername }?.let {
                        markSeen(it.id)
                    }
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun setupWebSocket() {
        val token = tokenManager.getToken() ?: return
        webSocketManager = WebSocketManager(token)
        webSocketManager?.onMessageReceived = { message ->
            if (message.sender == partnerUsername || message.recipient == partnerUsername) {
                _messages.value = _messages.value + message
                if (message.sender == partnerUsername) {
                    markDelivered(message.id)
                    markSeen(message.id)
                }
            }
        }
        webSocketManager?.onStatusUpdateReceived = { update ->
            _messages.value = _messages.value.map { msg ->
                if (msg.id == update.messageId) {
                    msg.copy(
                        status = update.status,
                        deliveredAt = update.deliveredAt ?: msg.deliveredAt,
                        seenAt = update.seenAt ?: msg.seenAt
                    )
                } else {
                    msg
                }
            }
        }
        webSocketManager?.connect()
    }

    fun sendMessage(content: String) {
        if (content.isBlank()) return
        webSocketManager?.sendMessage(partnerUsername, content)
    }

    private fun markDelivered(messageId: Long) {
        webSocketManager?.markDelivered(messageId)
    }

    private fun markSeen(messageId: Long) {
        webSocketManager?.markSeen(messageId)
    }

    override fun onCleared() {
        super.onCleared()
        webSocketManager?.disconnect()
    }
}