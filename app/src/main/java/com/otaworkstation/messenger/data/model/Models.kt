package com.otaworkstation.messenger.data.model

import com.google.gson.annotations.SerializedName

data class AuthRequest(
    val username: String,
    val password: String,
    val email: String? = null
)

data class LoginResponse(
    val token: String
)

data class RegisterResponse(
    val username: String
)

data class ErrorResponse(
    val error: String
)

data class Message(
    val id: Long,
    val sender: String,
    val recipient: String,
    val content: String,
    val status: MessageStatus,
    val sentAt: String,
    val deliveredAt: String?,
    val seenAt: String?,
    val attachmentUrl: String?,
    val attachmentName: String?,
    val attachmentType: String?,
    val attachmentSize: Long?
)

enum class MessageStatus {
    SENT, DELIVERED, SEEN
}

data class ConversationDTO(
    val partner: String,
    val lastMessageAt: String
)

data class PaginatedResponse<T>(
    val content: List<T>,
    val totalElements: Int,
    val totalPages: Int,
    val last: Boolean,
    val first: Boolean,
    val numberOfElements: Int
)

data class ProfileDTO(
    val username: String,
    val displayName: String?,
    val bio: String?,
    val profilePictureUrl: String?
)

data class ProfileUpdateRequest(
    val displayName: String? = null,
    val bio: String? = null
)

data class AttachmentUploadResponse(
    val attachmentUrl: String,
    val attachmentName: String,
    val attachmentType: String,
    val attachmentSize: Long
)

data class PresenceResponse(
    val username: String,
    val online: Boolean
)

data class OnlineUsersResponse(
    val onlineUsers: List<String>
)

data class StatusUpdate(
    val messageId: Long,
    val status: MessageStatus,
    val deliveredAt: String? = null,
    val seenAt: String? = null
)
