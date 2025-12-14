package com.example.sorms_app.data.repository

import com.example.sorms_app.data.datasource.local.AuthSession
import com.example.sorms_app.domain.model.User
import com.example.sorms_app.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor() : UserRepository {
    override fun getCurrentUser(): Flow<User?> = flow {
        // In a real app, you might fetch more details from a remote source.
        // For now, we use the data stored in the session after login.
        val accountId = AuthSession.accountId
        if (accountId != null) {
            val user = User(
                id = accountId,
                name = AuthSession.userName ?: "(Không có tên)",
                email = AuthSession.userEmail ?: "(Không có email)",
                avatarUrl = AuthSession.avatarUrl
            )
            emit(user)
        } else {
            emit(null) // No user logged in
        }
    }
}
