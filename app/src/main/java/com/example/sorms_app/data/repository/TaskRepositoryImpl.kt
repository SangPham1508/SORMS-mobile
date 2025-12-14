package com.example.sorms_app.data.repository

import com.example.sorms_app.data.datasource.remote.TaskApiService
import com.example.sorms_app.data.datasource.remote.TaskResponse
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
            val response = api.getMyTasks()
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
        // This is a mock implementation. In a real app, you would fetch a single task from the API.
        // For now, we find it in the full list.
        getTasks().collect { tasks ->
            emit(tasks.find { it.id == taskId })
        }
    }

    override suspend fun updateTaskStatus(taskId: String, status: Status) {
        try {
            val response = api.updateTaskStatus(taskId, status.name)
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
            id = this.id,
            title = this.title,
            description = this.description,
            priority = when (this.priority.uppercase()) {
                "HIGH" -> Priority.HIGH
                "MEDIUM" -> Priority.MEDIUM
                else -> Priority.LOW
            },
            status = when (this.status.uppercase()) {
                "IN_PROGRESS" -> Status.IN_PROGRESS
                "COMPLETED" -> Status.COMPLETED
                "REJECTED" -> Status.REJECTED
                else -> Status.PENDING
            },
            dueDate = this.dueDate?.let { 
                try { isoFormatter.parse(it) } catch (e: Exception) { null }
            },
            assignedBy = this.assignedBy
        )
    }
}
