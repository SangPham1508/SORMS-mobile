package com.example.sorms_app.domain.repository

import com.example.sorms_app.domain.model.Booking
import com.example.sorms_app.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface BookingRepository {
    fun getCurrentBooking(): Flow<Booking?>
}

interface NotificationRepository {
    fun getRecentNotifications(): Flow<List<Notification>>
}



