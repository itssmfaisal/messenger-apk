package com.otaworkstation.messenger.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import com.otaworkstation.messenger.util.TokenManager
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Provide a Coil ImageLoader that attaches Authorization header using the app TokenManager.
 * Use `rememberAuthImageLoader()` inside composables and pass it to AsyncImage via imageLoader parameter.
 */
@Composable
fun rememberAuthImageLoader(): ImageLoader {
    val context: Context = LocalContext.current
    return remember(context) {
        val tokenManager = TokenManager(context)
        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val token = tokenManager.getToken()
            val builder = original.newBuilder()
            if (!token.isNullOrBlank()) {
                builder.header("Authorization", "Bearer $token")
            }
            chain.proceed(builder.build())
        }

        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()

        ImageLoader.Builder(context)
            .okHttpClient(client)
            .build()
    }
}


