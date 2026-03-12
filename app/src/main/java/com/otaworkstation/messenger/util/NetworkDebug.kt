package com.otaworkstation.messenger.util

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Simple helper that issues a HEAD request to the given URL using the TokenManager to set
 * Authorization header when available. Returns HTTP status code or null on failure and an error message.
 */
suspend fun checkUrlStatus(context: Context, url: String): Pair<Int?, String?> {
    return try {
        val tokenManager = TokenManager(context)
        val token = tokenManager.getToken()

        val client = OkHttpClient.Builder()
            .build()

        val builder = Request.Builder()
            .url(url)
            .head()

        if (!token.isNullOrBlank()) {
            builder.addHeader("Authorization", "Bearer $token")
        }

        val request = builder.build()
        val resp = client.newCall(request).execute()
        resp.use {
            Pair(it.code, null)
        }
    } catch (e: Exception) {
        Pair(null, e.message)
    }
}

