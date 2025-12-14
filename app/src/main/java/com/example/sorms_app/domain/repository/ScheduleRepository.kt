package com.example.sorms_app.domain.repository

import com.example.sorms_app.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    fun getScheduledTasks(): Flow<Map<String, List<Task>>>
}



