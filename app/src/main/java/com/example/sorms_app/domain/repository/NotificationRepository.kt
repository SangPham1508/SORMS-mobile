package com.example.sorms_app.domain.repository

import com.example.sorms_app.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getRecentNotifications(): Flow<List<Notification>>
}



