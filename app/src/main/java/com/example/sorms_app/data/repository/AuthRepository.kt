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
            val savedRefreshToken = tokenManager.refreshTokenFlow.first()
            val savedAccountId = tokenManager.accountIdFlow.first()
            val savedUserName = tokenManager.userNameFlow.first()
            val savedUserEmail = tokenManager.userEmailFlow.first()
            val savedAvatarUrl = tokenManager.avatarUrlFlow.first()
            val savedRoles = tokenManager.rolesFlow.first()
            
            if (savedToken.isNullOrBlank()) {
                return@withContext AuthResult.Error("No saved token")
            }

            // Restore all session data to in-memory
            AuthSession.currentToken = savedToken
            AuthSession.accountId = savedAccountId
            AuthSession.userName = savedUserName
            AuthSession.userEmail = savedUserEmail
            AuthSession.avatarUrl = savedAvatarUrl
            AuthSession.roles = savedRoles

            // Try to validate token by refreshing if we have refresh token
            // This ensures token is still valid
            if (!savedRefreshToken.isNullOrBlank()) {
                try {
                    val refreshResult = refreshToken()
                    if (refreshResult is AuthResult.Success) {
                        return@withContext refreshResult
                    }
                    // If refresh fails, token might be expired
                    // But we'll still try to use saved token - interceptor will handle 401
                } catch (e: Exception) {
                    // Refresh failed, but continue with saved token
                    // Interceptor will handle 401 errors
                }
            }
            
            // Return success with saved roles (or default to USER)
            val roles = savedRoles.ifEmpty { listOf("USER") }
            return@withContext AuthResult.Success(roles)
            
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
                    val userName = "${accountInfo.firstName ?: ""} ${accountInfo.lastName ?: ""}".trim()
                    
                    // Persist tokens and user info to DataStore
                    tokenManager.saveTokens(
                        accessToken = token,
                        refreshToken = refreshToken,
                        accountId = accountInfo.id,
                        userName = userName,
                        userEmail = accountInfo.email,
                        avatarUrl = accountInfo.avatarUrl,
                        roles = roles
                    )

                    // Keep user info in-memory for fast access
                    AuthSession.currentToken = token
                    AuthSession.accountId = accountInfo.id
                    AuthSession.userName = userName
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
                    val userName = "${accountInfo.firstName ?: ""} ${accountInfo.lastName ?: ""}".trim()
                    
                    // Persist new tokens and user info to DataStore
                    tokenManager.saveTokens(
                        accessToken = newToken,
                        refreshToken = newRefreshToken,
                        accountId = accountInfo.id,
                        userName = userName,
                        userEmail = accountInfo.email,
                        avatarUrl = accountInfo.avatarUrl,
                        roles = roles
                    )

                    // Update in-memory session
                    AuthSession.currentToken = newToken
                    AuthSession.accountId = accountInfo.id
                    AuthSession.userName = userName
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
