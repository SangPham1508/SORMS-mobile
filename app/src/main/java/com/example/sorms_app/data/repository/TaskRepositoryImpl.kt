package com.example.sorms_app.data.repository

import com.example.sorms_app.data.datasource.local.AuthSession
import com.example.sorms_app.data.datasource.remote.TaskApiService
import com.example.sorms_app.data.datasource.remote.TaskResponse
import com.example.sorms_app.data.datasource.remote.UpdateTaskRequest
import com.example.sorms_app.domain.model.Priority
import com.example.sorms_app.domain.model.Status
import com.example.sorms_app.domain.model.Task
import com.example.sorms_app.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val api: TaskApiService
) : TaskRepository {

    override fun getTasks(): Flow<List<Task>> = flow {
        try {
            // Get current staff ID from session
            val staffId = AuthSession.accountId?.toLongOrNull()
            if (staffId == null) {
                emit(emptyList())
                return@flow
            }

            val response = api.getTasksByAssignee(staffId)
            if (response.isSuccessful) {
                val taskResponses = response.body()?.data ?: emptyList()
                val tasks = taskResponses.map { it.toDomainModel() }
                emit(tasks)
            } else {
                throw Exception("Failed to fetch tasks: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            throw Exception("Network error fetching tasks: ${e.message}")
        }
    }

    override fun getTaskById(taskId: String): Flow<Task?> = flow {
        try {
            val taskIdLong = taskId.toLongOrNull()
            if (taskIdLong == null) {
                emit(null)
                return@flow
            }

            val response = api.getTaskById(taskIdLong)
            if (response.isSuccessful) {
                val taskResponse = response.body()?.data
                emit(taskResponse?.toDomainModel())
            } else {
                throw Exception("Failed to fetch task: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            throw Exception("Network error fetching task: ${e.message}")
        }
    }

    override suspend fun updateTaskStatus(taskId: String, status: Status) {
        try {
            val taskIdLong = taskId.toLongOrNull()
            if (taskIdLong == null) {
                throw Exception("Invalid task ID: $taskId")
            }

            val request = UpdateTaskRequest(
                id = taskIdLong,
                status = status.name
            )

            val response = api.updateTask(taskIdLong, request)
            if (!response.isSuccessful) {
                throw Exception("Failed to update task status: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            throw Exception("Network error updating task: ${e.message}")
        }
    }

    private fun TaskResponse.toDomainModel(): Task {
        val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        
        return Task(
            id = this.id.toString(),
            title = this.title,
            description = this.description,
            priority = when (this.priority?.uppercase()) {
                "HIGH" -> Priority.HIGH
                "MEDIUM" -> Priority.MEDIUM
                else -> Priority.LOW
            },
            status = when (this.status?.uppercase()) {
                "IN_PROGRESS" -> Status.IN_PROGRESS
                "COMPLETED" -> Status.COMPLETED
                "REJECTED" -> Status.REJECTED
                "OPEN" -> Status.PENDING
                else -> Status.PENDING
            },
            dueDate = this.dueAt?.let { 
                try { isoFormatter.parse(it) } catch (e: Exception) { null }
            },
            assignedBy = this.taskCreatedBy?.toString()
        )
    }
}
