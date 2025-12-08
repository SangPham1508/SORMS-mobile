package com.example.sorms_app.data.api

import android.util.Base64
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit Client để kết nối với API backend
 * 
 * API sử dụng Basic Authentication
 */
object RetrofitClient {

    // Base URL của API
    private const val BASE_URL = "http://103.81.87.99:5656/api/"

    // Thông tin đăng nhập Basic Auth - THAY ĐỔI THEO THÔNG TIN CỦA BẠN
    private const val API_USERNAME = "admin"  // TODO: Thay username của bạn
    private const val API_PASSWORD = "admin"  // TODO: Thay password của bạn

    // Tạo Basic Auth header
    private fun createBasicAuthHeader(): String {
        val credentials = "$API_USERNAME:$API_PASSWORD"
        val encoded = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
        return "Basic $encoded"
    }

    // Interceptor thêm header Authorization với Basic Auth
    private val authInterceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()
        
        // Thêm Basic Authentication header
        if (API_USERNAME.isNotBlank() && API_PASSWORD.isNotBlank()) {
            requestBuilder.addHeader("Authorization", createBasicAuthHeader())
        }
        
        // Thêm Accept để server trả JSON
        requestBuilder.addHeader("Accept", "application/json")
        chain.proceed(requestBuilder.build())
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val roomApiService: RoomApiService by lazy {
        retrofit.create(RoomApiService::class.java)
    }
}




