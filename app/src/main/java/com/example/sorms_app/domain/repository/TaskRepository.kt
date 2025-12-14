package com.example.sorms_app.domain.repository

import com.example.sorms_app.domain.model.Status
import com.example.sorms_app.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasks(): Flow<List<Task>>
    fun getTaskById(taskId: String): Flow<Task?>
    suspend fun updateTaskStatus(taskId: String, status: Status)
}
