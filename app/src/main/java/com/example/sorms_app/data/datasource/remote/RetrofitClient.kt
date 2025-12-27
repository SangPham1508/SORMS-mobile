package com.example.sorms_app.data.datasource.remote

import android.content.Context
import com.example.sorms_app.BuildConfig
import com.example.sorms_app.data.datasource.local.AuthSession
import com.example.sorms_app.data.repository.AuthRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = BuildConfig.API_BASE_URL

    @Volatile private var tokenProvider: (() -> String?)? = null
    @Volatile private var refreshTokenProvider: (() -> String?)? = null
    @Volatile private var contextProvider: (() -> Context?)? = null

    fun init(getToken: () -> String?) {
        tokenProvider = getToken
    }

    fun initRefreshToken(getRefreshToken: () -> String?) {
        refreshTokenProvider = getRefreshToken
    }

    fun initContext(getContext: () -> Context?) {
        contextProvider = getContext
    }

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        
        // Skip auth for login/refresh endpoints
        val url = original.url.toString()
        if (url.contains("/auth/mobile/outbound/authentication") || 
            url.contains("/auth/refresh")) {
            return@Interceptor chain.proceed(original)
        }

        val builder = original.newBuilder()
            .addHeader("Accept", "application/json")

        tokenProvider?.invoke()?.takeIf { it.isNotBlank() }?.let { token ->
            builder.addHeader("Authorization", "Bearer $token")
        }

        val response = chain.proceed(builder.build())

        // If 401 Unauthorized, try to refresh token
        if (response.code == 401) {
            response.close()
            
            val context = contextProvider?.invoke()
            if (context != null) {
                try {
                    val authRepository = AuthRepository(context)
                    val refreshResult = runBlocking {
                        authRepository.refreshToken()
                    }
                    
                    if (refreshResult is com.example.sorms_app.data.repository.AuthResult.Success) {
                        // Retry original request with new token
                        val newToken = tokenProvider?.invoke()
                        val newRequest = original.newBuilder()
                            .header("Accept", "application/json")
                            .header("Authorization", "Bearer $newToken")
                            .build()
                        return@Interceptor chain.proceed(newRequest)
                    } else {
                        // Refresh failed, clear session
                        runBlocking {
                            authRepository.logout()
                        }
                    }
                } catch (e: Exception) {
                    // Refresh failed, clear session
                    val context = contextProvider?.invoke()
                    if (context != null) {
                        try {
                            runBlocking {
                                AuthRepository(context).logout()
                            }
                        } catch (_: Exception) {}
                    }
                }
            }
        }

        response
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // Use HEADERS for debugging, NONE for production to avoid ANR from excessive logging
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.HEADERS else HttpLoggingInterceptor.Level.NONE
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

    val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val bookingApiService: BookingApiService by lazy {
        retrofit.create(BookingApiService::class.java)
    }

    val roomTypeApiService: RoomTypeApiService by lazy {
        retrofit.create(RoomTypeApiService::class.java)
    }

    val taskApiService: TaskApiService by lazy {
        retrofit.create(TaskApiService::class.java)
    }

    val notificationApiService: NotificationApiService by lazy {
        retrofit.create(NotificationApiService::class.java)
    }

    val serviceApiService: ServiceApiService by lazy {
        retrofit.create(ServiceApiService::class.java)
    }

    val orderApiService: OrderApiService by lazy {
        retrofit.create(OrderApiService::class.java)
    }

    val userApiService: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }

    val faceRecognitionApiService: FaceRecognitionApiService by lazy {
        retrofit.create(FaceRecognitionApiService::class.java)
    }
}
