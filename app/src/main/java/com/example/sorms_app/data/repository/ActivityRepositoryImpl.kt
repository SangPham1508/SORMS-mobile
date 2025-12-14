package com.example.sorms_app.data.repository

import com.example.sorms_app.data.datasource.local.AuthSession
import com.example.sorms_app.data.datasource.remote.BookingApiService
import com.example.sorms_app.data.models.BookingResponse
import com.example.sorms_app.domain.model.Notification
import com.example.sorms_app.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ActivityRepositoryImpl @Inject constructor(
    private val bookingApi: BookingApiService
) : ActivityRepository {

    override fun getActivityHistory(): Flow<List<Notification>> = flow {
        val userId = AuthSession.accountId
        if (userId == null) {
            emit(emptyList())
            return@flow
        }

        try {
            val response = bookingApi.getBookingsByUser(userId)
            if (response.isSuccessful) {
                val bookingResponses = response.body()?.data ?: emptyList()
                // Map booking history to a list of activity items
                val activities = bookingResponses.map { it.toActivityNotification() }
                emit(activities)
            } else {
                throw Exception("Failed to fetch activity history: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            throw Exception("Network error fetching activity history: ${e.message}")
        }
    }

    private fun BookingResponse.toActivityNotification(): Notification {
        val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = this.checkinDate?.let { 
            try { isoFormatter.parse(it) } catch (e: Exception) { Date() }
        } ?: Date()

        val message = "Bạn đã đặt ${this.roomCode ?: "một phòng"} với trạng thái: ${this.status ?: "Không rõ"}"

        return Notification(
            id = this.id.toString(),
            message = message,
            date = date,
            isRead = false // Placeholder
        )
    }
}
