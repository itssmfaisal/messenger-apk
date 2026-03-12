package com.otaworkstation.messenger.ui.conversations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otaworkstation.messenger.data.model.ConversationDTO
import com.otaworkstation.messenger.data.repository.MessengerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

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
                    val convs = response.body()?.content ?: emptyList()
                    // Enrich conversations with partner profile pictures when available
                    val enriched = convs.map { conv ->
                        // Launch an async request for each partner's profile
                        async {
                            try {
                                val profileResp = repository.getUserProfile(conv.partner)
                                if (profileResp.isSuccessful) {
                                    val profile = profileResp.body()
                                    conv.copy(partnerProfilePictureUrl = profile?.profilePictureUrl)
                                } else conv
                            } catch (e: Exception) {
                                conv
                            }
                        }
                    }.awaitAll()

                    _conversations.value = enriched
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}