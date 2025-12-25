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
        
        // Initialize Retrofit token provider with in-memory session token
        RetrofitClient.init { AuthSession.currentToken }
        
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
