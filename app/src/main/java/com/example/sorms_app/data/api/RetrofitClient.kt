package com.example.sorms_app.data.api

import com.example.sorms_app.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit Client để kết nối với API backend
 *
 * ⚠️ Thiết lập token trong BuildConfig.API_TOKEN (gradle buildConfigField)
 */
object RetrofitClient {

    // Lấy URL từ BuildConfig để dễ cấu hình
    private const val DEFAULT_BASE_URL = "http://103.81.87.99:5656/api/"
    private val BASE_URL = (BuildConfig.API_BASE_URL.takeIf { it.isNotBlank() } ?: DEFAULT_BASE_URL)
        .let { if (it.endsWith("/")) it else "$it/" }

    // Interceptor thêm header Authorization nếu có token
    private val authInterceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()
        val token = BuildConfig.API_TOKEN
        if (token.isNotBlank()) {
            requestBuilder.addHeader("Authorization", token)
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



