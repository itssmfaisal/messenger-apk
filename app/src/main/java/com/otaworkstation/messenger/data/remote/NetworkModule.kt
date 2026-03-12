package com.otaworkstation.messenger.data.remote

import com.otaworkstation.messenger.util.Constants
import com.otaworkstation.messenger.util.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkModule(private val tokenManager: TokenManager) {

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val token = tokenManager.getToken()
        
        val requestBuilder = original.newBuilder()
        if (token != null) {
            requestBuilder.header("Authorization", "Bearer $token")
        }
        
        chain.proceed(requestBuilder.build())
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    val api: MessengerApi = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
        .create(MessengerApi::class.java)
}