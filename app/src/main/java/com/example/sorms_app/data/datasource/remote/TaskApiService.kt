package com.example.sorms_app.data.datasource.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface TaskApiService {

    @GET("tasks/assigned-to/me") // Assuming an endpoint to get tasks assigned to the current user
    suspend fun getMyTasks(): Response<ApiResponse<List<TaskResponse>>>

    @PUT("tasks/{id}/status/{status}")
    suspend fun updateTaskStatus(@Path("id") taskId: String, @Path("status") status: String): Response<ApiResponse<TaskResponse>>
}

data class TaskResponse(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("priority") val priority: String, // e.g., "HIGH", "MEDIUM", "LOW"
    @SerializedName("status") val status: String, // e.g., "PENDING", "IN_PROGRESS", "COMPLETED"
    @SerializedName("dueDate") val dueDate: String?, // ISO-8601 date string
    @SerializedName("assignedBy") val assignedBy: String?
)



