package com.example.sorms_app.data.datasource.local

// Simple in-memory session holder. Consider making it reactive if needed.
object AuthSession {
    @Volatile var currentToken: String? = null
    @Volatile var accountId: String? = null
    @Volatile var userName: String? = null
    @Volatile var userEmail: String? = null
    @Volatile var avatarUrl: String? = null
    @Volatile var roles: List<String> = emptyList()

    fun clear() {
        currentToken = null
        accountId = null
        userName = null
        userEmail = null
        avatarUrl = null
        roles = emptyList()
    }
}
