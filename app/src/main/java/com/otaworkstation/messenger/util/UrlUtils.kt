package com.otaworkstation.messenger.util

import java.net.URI

object UrlUtils {
    /**
     * Resolve a possibly-relative media URL returned by the backend into a full absolute URL.
     * - If input is null/blank, returns null.
     * - If input already starts with http:// or https:// returns it unchanged.
     * - Otherwise it will derive the origin (scheme://host[:port]) from Constants.BASE_URL and
     *   join the path.
     */
    fun resolveMediaUrl(url: String?): String? {
        if (url.isNullOrBlank()) return null
        val trimmed = url.trim()
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) return trimmed

        return try {
            val baseUri = URI(Constants.BASE_URL)
            val originWithApi = StringBuilder().apply {
                append(baseUri.scheme)
                append("://")
                append(baseUri.host)
                if (baseUri.port != -1) append(":").append(baseUri.port)
                append("/api")
            }.toString()
            // Always resolve under /api for media files
            val resolvedUrl = if (trimmed.startsWith("/")) originWithApi + trimmed else originWithApi + "/" + trimmed
            resolvedUrl
        } catch (e: Exception) {
            // Fallback: just return the original value if parsing fails
            trimmed
        }
    }
}

