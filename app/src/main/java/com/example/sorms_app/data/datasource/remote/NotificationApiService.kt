package com.example.sorms_app.data.datasource.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET

interface NotificationApiService {

    @GET("notifications/me") // Assuming an endpoint to get notifications for the current user
    suspend fun getMyNotifications(): Response<ApiResponse<List<NotificationResponse>>>
}

data class NotificationResponse(
    @SerializedName("id") val id: String,
    @SerializedName("message") val message: String,
    @SerializedName("createdAt") val createdAt: String, // ISO-8601 date string
    @SerializedName("isRead") val isRead: Boolean
)



