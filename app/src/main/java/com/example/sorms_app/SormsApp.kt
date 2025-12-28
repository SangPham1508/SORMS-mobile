package com.example.sorms_app

import android.app.Application
import com.example.sorms_app.data.datasource.local.AuthSession
import com.example.sorms_app.data.datasource.local.TokenManager
import com.example.sorms_app.data.datasource.remote.RetrofitClient
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@HiltAndroidApp
class SormsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val tokenManager = TokenManager(this)
        
        // Load token from DataStore into AuthSession when app starts
        // Đảm bảo token được restore vào AuthSession để RetrofitClient có thể sử dụng
        runBlocking {
            try {
                val savedToken = tokenManager.accessTokenFlow.first()
                val savedAccountId = tokenManager.accountIdFlow.first()
                val savedUserName = tokenManager.userNameFlow.first()
                val savedUserEmail = tokenManager.userEmailFlow.first()
                val savedAvatarUrl = tokenManager.avatarUrlFlow.first()
                val savedRoles = tokenManager.rolesFlow.first()
                
                if (!savedToken.isNullOrBlank()) {
                    AuthSession.currentToken = savedToken
                    AuthSession.accountId = savedAccountId
                    AuthSession.userName = savedUserName
                    AuthSession.userEmail = savedUserEmail
                    AuthSession.avatarUrl = savedAvatarUrl
                    // rolesFlow already returns List<String>, no need to split
                    AuthSession.roles = savedRoles
                    android.util.Log.d("SormsApp", "Restored token from DataStore: ${savedToken.take(20)}...")
                } else {
                    android.util.Log.w("SormsApp", "No saved token found in DataStore")
                }
            } catch (e: Exception) {
                android.util.Log.e("SormsApp", "Error restoring token", e)
            }
        }
        
        // Initialize Retrofit token provider with in-memory session token
        // Token sẽ được lấy từ AuthSession.currentToken mỗi lần request (không cache)
        // Đảm bảo luôn lấy token mới nhất từ AuthSession
        RetrofitClient.init { 
            // Luôn lấy token mới từ AuthSession (không cache)
            val token = AuthSession.currentToken?.takeIf { it.isNotBlank() }
            if (token == null) {
                android.util.Log.w("RetrofitClient", "Token provider called but AuthSession.currentToken is null/empty")
            }
            token
        }
        
        // Initialize refresh token provider
        RetrofitClient.initRefreshToken {
            runBlocking {
                tokenManager.refreshTokenFlow.first()
            }
        }
        
        // Initialize context provider for refresh token logic
        RetrofitClient.initContext { this }
    }
}
