package com.example.sorms_app.data.datasource.remote

import com.example.sorms_app.data.datasource.remote.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// Backend auth endpoints mapping AuthenticationController
interface AuthApiService {

    // POST /auth/mobile/outbound/authentication
    @POST("auth/mobile/outbound/authentication")
    suspend fun mobileAuthenticate(@Body request: MobileOutboundAuthenticateRequest): Response<ApiResponse<AuthenticationResponse>>

    // POST /auth/refresh
    @POST("auth/refresh")
    suspend fun refresh(@Body request: RefreshTokenRequest): Response<ApiResponse<AuthenticationResponse>>
}

// Requests
data class MobileOutboundAuthenticateRequest(
    val idToken: String,
    val platform: String = "android"
)

data class RefreshTokenRequest(
    val refreshToken: String   
)

// Responses matching backend models
data class AuthenticationResponse(
    val authenticated: Boolean,
    val token: String?,
    val refreshToken: String?,
    val accountInfo: AccountInfoAuthenticateDTO?
)

data class AccountInfoAuthenticateDTO(
    val id: String?,
    val email: String?,
    val firstName: String?,
    val lastName: String?,
    val avatarUrl: String?,
    val roles: List<String>?
)


