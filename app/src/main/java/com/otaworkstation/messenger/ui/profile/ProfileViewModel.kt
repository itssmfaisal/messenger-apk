package com.otaworkstation.messenger.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otaworkstation.messenger.data.model.ProfileDTO
import com.otaworkstation.messenger.data.model.ProfileUpdateRequest
import com.otaworkstation.messenger.data.repository.MessengerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ProfileViewModel(private val repository: MessengerRepository) : ViewModel() {

    private val _profile = MutableStateFlow<ProfileDTO?>(null)
    val profile = _profile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getOwnProfile()
                if (response.isSuccessful) {
                    _profile.value = response.body()
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(displayName: String?, bio: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.updateProfile(ProfileUpdateRequest(displayName, bio))
                if (response.isSuccessful) {
                    _profile.value = response.body()
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}