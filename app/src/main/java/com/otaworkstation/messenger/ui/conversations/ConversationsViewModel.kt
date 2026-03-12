package com.otaworkstation.messenger.ui.conversations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otaworkstation.messenger.data.model.ConversationDTO
import com.otaworkstation.messenger.data.repository.MessengerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConversationsViewModel(private val repository: MessengerRepository) : ViewModel() {

    private val _conversations = MutableStateFlow<List<ConversationDTO>>(emptyList())
    val conversations = _conversations.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun loadConversations() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getConversations(0, 50)
                if (response.isSuccessful) {
                    _conversations.value = response.body()?.content ?: emptyList()
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}