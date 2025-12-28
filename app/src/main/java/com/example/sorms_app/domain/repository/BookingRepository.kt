package com.example.sorms_app.domain.repository

import com.example.sorms_app.domain.model.Booking
import kotlinx.coroutines.flow.Flow

interface BookingRepository {
    fun getCurrentBooking(): Flow<Booking?>
    fun getAllBookings(): Flow<List<Booking>>
    fun getUserBookings(): Flow<List<Booking>>

    suspend fun createBooking(
        roomId: Long,
        checkinDate: String,
        checkoutDate: String,
        numGuests: Int = 1,
        note: String? = null
    ): kotlin.Result<Booking>

    suspend fun cancelBooking(bookingId: Long): kotlin.Result<Unit>

    suspend fun updateBooking(
        bookingId: Long,
        checkinDate: String? = null,
        checkoutDate: String? = null,
        numGuests: Int? = null,
        note: String? = null
    ): kotlin.Result<Booking>
}
