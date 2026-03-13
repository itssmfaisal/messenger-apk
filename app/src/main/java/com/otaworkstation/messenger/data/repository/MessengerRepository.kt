package com.otaworkstation.messenger.data.repository

import com.otaworkstation.messenger.data.model.*
import com.otaworkstation.messenger.data.remote.MessengerApi
import okhttp3.MultipartBody
import retrofit2.Response

class MessengerRepository(private val api: MessengerApi) {

    suspend fun register(request: AuthRequest): Response<RegisterResponse> = api.register(request)

    suspend fun login(request: AuthRequest): Response<LoginResponse> = api.login(request)

    suspend fun getConversations(page: Int, size: Int): Response<PaginatedResponse<ConversationDTO>> =
        api.getConversations(page, size)

    suspend fun getConversationHistory(withUser: String, page: Int, size: Int): Response<PaginatedResponse<Message>> =
        api.getConversationHistory(withUser, page, size)

    suspend fun getOwnProfile(): Response<ProfileDTO> = api.getOwnProfile()

    suspend fun getUserProfile(username: String): Response<ProfileDTO> = api.getUserProfile(username)

    suspend fun updateProfile(request: ProfileUpdateRequest): Response<ProfileDTO> = api.updateProfile(request)

    suspend fun uploadProfilePicture(file: MultipartBody.Part): Response<ProfileDTO> = api.uploadProfilePicture(file)

    suspend fun uploadAttachment(token: String, file: MultipartBody.Part): Response<AttachmentUploadResponse> =
        api.uploadAttachment("Bearer $token", file)
}