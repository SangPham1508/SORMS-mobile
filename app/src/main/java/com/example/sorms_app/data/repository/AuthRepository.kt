package com.example.sorms_app.data.repository

import android.content.Context
import com.example.sorms_app.data.datasource.local.AuthSession
import com.example.sorms_app.data.datasource.local.TokenManager
import com.example.sorms_app.data.datasource.remote.OutboundAuthenticateRequest
import com.example.sorms_app.data.datasource.remote.RefreshTokenRequest
import com.example.sorms_app.data.datasource.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

sealed class AuthResult {
    data class Success(val roles: List<String>) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthRepository(private val context: Context) {
    private val api = RetrofitClient.authApiService
    private val tokenManager = TokenManager(context)

    /**
     * Check if user is already logged in (has valid token)
     * Call this when app starts to restore session
     */
    suspend fun checkExistingSession(): AuthResult = withContext(Dispatchers.IO) {
        try {
            val savedToken = tokenManager.accessTokenFlow.first()
            
            if (savedToken.isNullOrBlank()) {
                return@withContext AuthResult.Error("No saved token")
            }

            // Restore token to in-memory session
            AuthSession.currentToken = savedToken

            // Try to introspect/validate the token by making a request
            // For now, we'll trust the saved token and try to use it
            // If it's invalid, API calls will fail and user will be logged out
            
            // Try to get user info from a lightweight endpoint or cache
            // For simplicity, we'll return success with empty roles
            // The actual roles will be fetched when needed
            
            // In a real app, you might want to:
            // 1. Call an introspect endpoint to validate the token
            // 2. Store user info in DataStore as well
            
            return@withContext AuthResult.Success(AuthSession.roles.ifEmpty { listOf("USER") })
            
        } catch (e: Exception) {
            return@withContext AuthResult.Error("Không thể khôi phục phiên: ${e.message}")
        }
    }

    /**
     * Login với Authorization Code (giống web) - gọi /auth/outbound/authentication
     */
    suspend fun loginWithAuthCode(authCode: String, redirectUri: String): AuthResult = withContext(Dispatchers.IO) {
        try {
            val response = api.outboundAuthenticate(
                OutboundAuthenticateRequest(
                    code = authCode,
                    redirectUri = redirectUri
                )
            )

            if (response.isSuccessful) {
                val body = response.body()?.data
                val token = body?.token
                val refreshToken = body?.refreshToken
                val accountInfo = body?.accountInfo
                val roles = accountInfo?.roles ?: emptyList()

                if (!token.isNullOrBlank() && accountInfo != null) {
                    // Persist both access token and refresh token to DataStore
                    tokenManager.saveTokens(token, refreshToken)

                    // Keep user info in-memory for fast access
                    AuthSession.currentToken = token
                    AuthSession.accountId = accountInfo.id
                    AuthSession.userName = "${accountInfo.firstName ?: ""} ${accountInfo.lastName ?: ""}".trim()
                    AuthSession.userEmail = accountInfo.email
                    AuthSession.avatarUrl = accountInfo.avatarUrl
                    AuthSession.roles = roles

                    return@withContext AuthResult.Success(roles)
                }
                return@withContext AuthResult.Error("Token hoặc thông tin người dùng trống từ backend")
            } else {
                val err = response.errorBody()?.string()
                return@withContext AuthResult.Error("Đăng nhập thất bại: ${response.code()} - ${err ?: response.message()}")
            }
        } catch (e: Exception) {
            return@withContext AuthResult.Error(e.localizedMessage ?: "Lỗi không xác định")
        }
    }

    /**
     * Refresh access token using refresh token
     */
    suspend fun refreshToken(): AuthResult = withContext(Dispatchers.IO) {
        try {
            val savedRefreshToken = tokenManager.refreshTokenFlow.first()
            
            if (savedRefreshToken.isNullOrBlank()) {
                return@withContext AuthResult.Error("Không có refresh token")
            }

            val response = api.refresh(RefreshTokenRequest(refreshToken = savedRefreshToken))
            
            if (response.isSuccessful) {
                val body = response.body()?.data
                val newToken = body?.token
                val newRefreshToken = body?.refreshToken
                val accountInfo = body?.accountInfo
                val roles = accountInfo?.roles ?: emptyList()

                if (!newToken.isNullOrBlank() && accountInfo != null) {
                    // Persist new tokens to DataStore
                    tokenManager.saveTokens(newToken, newRefreshToken)

                    // Update in-memory session
                    AuthSession.currentToken = newToken
                    AuthSession.accountId = accountInfo.id
                    AuthSession.userName = "${accountInfo.firstName ?: ""} ${accountInfo.lastName ?: ""}".trim()
                    AuthSession.userEmail = accountInfo.email
                    AuthSession.avatarUrl = accountInfo.avatarUrl
                    AuthSession.roles = roles
                    
                    return@withContext AuthResult.Success(roles)
                }
                return@withContext AuthResult.Error("Token mới hoặc thông tin người dùng trống từ backend")
            } else {
                return@withContext AuthResult.Error("Refresh token thất bại: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            return@withContext AuthResult.Error(e.localizedMessage ?: "Lỗi không xác định khi refresh token")
        }
    }

    /**
     * Logout - clear all stored data
     */
    suspend fun logout() = withContext(Dispatchers.IO) {
        try {
            tokenManager.clear()
        } catch (_: Exception) {}
        AuthSession.clear()
    }
}
