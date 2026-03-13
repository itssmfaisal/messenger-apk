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

    private var currentPage = 0
    private var pageSize = 5
    private var lastPage = false
    private var loadingMore = false

    fun loadConversations() {
        currentPage = 0
        lastPage = false
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getConversations(currentPage, pageSize)
                if (response.isSuccessful) {
                    val resp = response.body()
                    val convs = resp?.content ?: emptyList()
                    lastPage = resp?.last ?: true
                    android.util.Log.d("ConversationsViewModel", "loadConversations: page=$currentPage, lastPage=$lastPage, totalPages=${resp?.totalPages}, totalElements=${resp?.totalElements}, loaded=${convs.size}")
                    val enriched = convs.map { conv ->
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
                    android.util.Log.d("ConversationsViewModel", "conversations after load: ${_conversations.value.size}")
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadNextPage() {
        if (lastPage || loadingMore) {
            android.util.Log.d("ConversationsViewModel", "loadNextPage: lastPage=$lastPage, loadingMore=$loadingMore, not loading next page")
            return
        }
        loadingMore = true
        viewModelScope.launch {
            try {
                val nextPage = currentPage + 1
                val response = repository.getConversations(nextPage, pageSize)
                if (response.isSuccessful) {
                    val resp = response.body()
                    val convs = resp?.content ?: emptyList()
                    lastPage = resp?.last ?: true
                    android.util.Log.d("ConversationsViewModel", "loadNextPage: page=$nextPage, lastPage=$lastPage, totalPages=${resp?.totalPages}, totalElements=${resp?.totalElements}, loaded=${convs.size}")
                    val enriched = convs.map { conv ->
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
                    _conversations.value = _conversations.value + enriched
                    currentPage = nextPage
                    android.util.Log.d("ConversationsViewModel", "conversations after loadNextPage: ${_conversations.value.size}")
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                loadingMore = false
            }
        }
    }
    fun canLoadMore(): Boolean = !lastPage && !loadingMore
}