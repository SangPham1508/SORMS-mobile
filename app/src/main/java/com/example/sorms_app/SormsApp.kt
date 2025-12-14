package com.example.sorms_app

import android.app.Application
import com.example.sorms_app.data.datasource.local.AuthSession
import com.example.sorms_app.data.datasource.remote.RetrofitClient
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SormsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Retrofit token provider with in-memory session token
        RetrofitClient.init { AuthSession.currentToken }
    }
}
