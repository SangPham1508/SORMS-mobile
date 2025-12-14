package com.example.sorms_app.data.repository

import android.content.Context
import com.example.sorms_app.data.datasource.local.AuthSession
import com.example.sorms_app.data.datasource.local.TokenManager
import com.example.sorms_app.data.datasource.remote.MobileOutboundAuthenticateRequest
import com.example.sorms_app.data.datasource.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class AuthResult {
    data class Success(val roles: List<String>) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthRepository(private val context: Context) {
    private val api = RetrofitClient.authApiService
    private val tokenManager = TokenManager(context)

    suspend fun loginWithGoogleIdToken(idToken: String): AuthResult = withContext(Dispatchers.IO) {
        try {
            val response = api.mobileAuthenticate(
                MobileOutboundAuthenticateRequest(idToken = idToken)
            )
            if (response.isSuccessful) {
                val body = response.body()?.data
                val token = body?.token
                val accountInfo = body?.accountInfo
                val roles = accountInfo?.roleName ?: emptyList()

                if (!token.isNullOrBlank() && accountInfo != null) {
                    // Persist token
                    tokenManager.saveTokens(token)

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
                return@withContext AuthResult.Error("Đăng nhập thất bại: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            return@withContext AuthResult.Error(e.localizedMessage ?: "Lỗi không xác định")
        }
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        try {
            tokenManager.clear()
        } catch (_: Exception) {}
        AuthSession.clear()
    }
}
