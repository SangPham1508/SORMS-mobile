package com.example.sorms_app.data.datasource.remote

import com.example.sorms_app.data.datasource.remote.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// Backend auth endpoints mapping AuthenticationController
interface AuthApiService {

    // POST /auth/outbound/authentication (web-style: authorization code flow)
    @POST("auth/outbound/authentication")
    suspend fun outboundAuthenticate(@Body request: OutboundAuthenticateRequest): Response<ApiResponse<AuthenticationResponse>>

    // POST /auth/refresh
    @POST("auth/refresh")
    suspend fun refresh(@Body request: RefreshTokenRequest): Response<ApiResponse<AuthenticationResponse>>
}

// Requests

data class OutboundAuthenticateRequest(
    val code: String,
    val redirectUri: String
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


