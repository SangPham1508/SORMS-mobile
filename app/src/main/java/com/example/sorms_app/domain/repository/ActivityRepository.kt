package com.example.sorms_app.domain.repository

import com.example.sorms_app.domain.model.Notification // Reusing Notification as ActivityItem for now
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {
    fun getActivityHistory(): Flow<List<Notification>>
}



