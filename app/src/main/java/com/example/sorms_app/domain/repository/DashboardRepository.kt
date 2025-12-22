package com.example.sorms_app.domain.repository

import com.example.sorms_app.domain.model.Booking
import kotlinx.coroutines.flow.Flow

interface BookingRepository {
    fun getCurrentBooking(): Flow<Booking?>
    fun getAllBookings(): Flow<List<Booking>>
    fun getUserBookings(): Flow<List<Booking>>  // Alias for getAllBookings for clarity
}



