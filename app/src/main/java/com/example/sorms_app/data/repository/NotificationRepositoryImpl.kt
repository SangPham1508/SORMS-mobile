package com.example.sorms_app.data.repository

import com.example.sorms_app.data.datasource.remote.NotificationApiService
import com.example.sorms_app.data.datasource.remote.NotificationResponse
import com.example.sorms_app.domain.model.Notification
import com.example.sorms_app.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val api: NotificationApiService
) : NotificationRepository {

    override fun getRecentNotifications(): Flow<List<Notification>> = flow {
        try {
            val response = api.getMyNotifications()
            if (response.isSuccessful) {
                val notificationResponses = response.body()?.data ?: emptyList()
                val notifications = notificationResponses.map { it.toDomainModel() }
                emit(notifications)
            } else {
                throw Exception("Failed to fetch notifications: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            throw Exception("Network error fetching notifications: ${e.message}")
        }
    }

    private fun NotificationResponse.toDomainModel(): Notification {
        val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return Notification(
            id = this.id,
            message = this.message,
            date = this.createdAt.let { 
                try { isoFormatter.parse(it) } catch (e: Exception) { Date() }
            } ?: Date(),
            isRead = this.isRead
        )
    }
}
