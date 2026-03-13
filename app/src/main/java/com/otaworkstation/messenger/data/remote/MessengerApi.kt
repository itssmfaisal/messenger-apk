package com.otaworkstation.messenger.data.remote

import com.otaworkstation.messenger.data.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface MessengerApi {

    @POST("auth/register")
    suspend fun register(@Body request: AuthRequest): Response<RegisterResponse>

    @POST("auth/login")
    suspend fun login(@Body request: AuthRequest): Response<LoginResponse>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: Map<String, String>): Response<Map<String, String>>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body request: Map<String, String>): Response<Map<String, String>>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: Map<String, String>): Response<Map<String, String>>

    @GET("messages/conversations")
    suspend fun getConversations(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<PaginatedResponse<ConversationDTO>>

    @GET("messages/conversation/{withUser}")
    suspend fun getConversationHistory(
        @Path("withUser") withUser: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<PaginatedResponse<Message>>

    @GET("presence/{username}")
    suspend fun getPresence(@Path("username") username: String): Response<PresenceResponse>

    @GET("presence")
    suspend fun getOnlineUsers(): Response<OnlineUsersResponse>

    @GET("profile")
    suspend fun getOwnProfile(): Response<ProfileDTO>

    @GET("profile/{username}")
    suspend fun getUserProfile(@Path("username") username: String): Response<ProfileDTO>

    @PUT("profile")
    suspend fun updateProfile(@Body request: ProfileUpdateRequest): Response<ProfileDTO>

    @Multipart
    @POST("profile/picture")
    suspend fun uploadProfilePicture(@Part file: MultipartBody.Part): Response<ProfileDTO>

    @POST("profile/email/send-otp")
    suspend fun sendEmailOtp(@Body request: Map<String, String>): Response<Map<String, String>>

    @POST("profile/email/verify")
    suspend fun verifyEmailOtp(@Body request: Map<String, String>): Response<ProfileDTO>

    @Multipart
    @POST("messages/attachment")
    suspend fun uploadAttachment(
        @Header("Authorization") authHeader: String,
        @Part file: MultipartBody.Part
    ): Response<AttachmentUploadResponse>
}