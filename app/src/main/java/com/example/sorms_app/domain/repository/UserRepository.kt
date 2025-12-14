package com.example.sorms_app.domain.repository

import com.example.sorms_app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getCurrentUser(): Flow<User?>
}


