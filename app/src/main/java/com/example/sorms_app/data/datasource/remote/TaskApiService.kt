package com.example.sorms_app.data.datasource.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.*

/**
 * API Service for Staff Tasks
 * Maps to backend StaffTaskController endpoints
 */
interface TaskApiService {

    // GET /staff-tasks/by-assignee/{assignedTo} - Get tasks assigned to a staff member
    @GET("staff-tasks/by-assignee/{assignedTo}")
    suspend fun getTasksByAssignee(@Path("assignedTo") assignedTo: Long): Response<ApiResponse<List<TaskResponse>>>

    // GET /staff-tasks/{id} - Get task by ID
    @GET("staff-tasks/{id}")
    suspend fun getTaskById(@Path("id") id: Long): Response<ApiResponse<TaskResponse>>

    // PUT /staff-tasks/{id} - Update task (including status)
    @PUT("staff-tasks/{id}")
    suspend fun updateTask(@Path("id") id: Long, @Body request: UpdateTaskRequest): Response<ApiResponse<TaskResponse>>

    // GET /staff-tasks/by-status?status=... - Get tasks by status
    @GET("staff-tasks/by-status")
    suspend fun getTasksByStatus(@Query("status") status: String): Response<ApiResponse<List<TaskResponse>>>

    // GET /staff-tasks/by-related?relatedType=...&relatedId=... - Get tasks by related entity
    @GET("staff-tasks/by-related")
    suspend fun getTasksByRelated(
        @Query("relatedType") relatedType: String,
        @Query("relatedId") relatedId: Long
    ): Response<ApiResponse<List<TaskResponse>>>

    // GET /staff-tasks - Get all tasks
    @GET("staff-tasks")
    suspend fun getAllTasks(): Response<ApiResponse<List<TaskResponse>>>
}

/**
 * Task response matching backend StaffTaskResponse
 */
data class TaskResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("relatedType") val relatedType: String?, // BOOKING, ORDER, MAINTENANCE
    @SerializedName("relatedId") val relatedId: Long?,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("assignedTo") val assignedTo: Long?,
    @SerializedName("taskCreatedBy") val taskCreatedBy: Long?,
    @SerializedName("priority") val priority: String?, // HIGH, MEDIUM, LOW
    @SerializedName("status") val status: String?, // OPEN, IN_PROGRESS, COMPLETED, CANCELLED
    @SerializedName("dueAt") val dueAt: String?, // ISO-8601 date string
    @SerializedName("createdDate") val createdDate: String?,
    @SerializedName("lastModifiedDate") val lastModifiedDate: String?
)

/**
 * Request model for updating a task
 */
data class UpdateTaskRequest(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("relatedType") val relatedType: String? = null,
    @SerializedName("relatedId") val relatedId: Long? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("assignedTo") val assignedTo: Long? = null,
    @SerializedName("taskCreatedBy") val taskCreatedBy: Long? = null,
    @SerializedName("priority") val priority: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("dueAt") val dueAt: String? = null // ISO-8601 date string
)



