package com.example.sorms_app.data.repository

import com.example.sorms_app.data.datasource.local.AuthSession
import com.example.sorms_app.data.datasource.remote.BookingApiService
import com.example.sorms_app.data.models.BookingResponse
import com.example.sorms_app.domain.model.Booking
import com.example.sorms_app.domain.repository.BookingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class BookingRepositoryImpl @Inject constructor(
    private val api: BookingApiService
) : BookingRepository {

    override fun getCurrentBooking(): Flow<Booking?> = flow {
        getAllBookings().collect { bookings ->
            val currentBooking = bookings.find { it.status.equals("CHECKED_IN", ignoreCase = true) }
            emit(currentBooking)
        }
    }

    override fun getAllBookings(): Flow<List<Booking>> = flow {
        val userId = AuthSession.accountId
        if (userId == null) {
            emit(emptyList())
            return@flow
        }

        try {
            val response = api.getBookingsByUser(userId)
            if (response.isSuccessful) {
                val bookings = response.body()?.data ?: emptyList()
                emit(bookings.map { it.toDomainModel() })
            } else {
                throw Exception("Failed to fetch bookings: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            throw Exception("Network error fetching bookings: ${e.message}")
        }
    }

    override fun getUserBookings(): Flow<List<Booking>> = getAllBookings()

    private fun BookingResponse.toDomainModel(): Booking {
        val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val bookingId = this.id ?: 0L
        return Booking(
            id = bookingId,
            code = this.code ?: "BK-$bookingId",
            roomName = this.roomCode ?: "(Không có phòng)",
            buildingName = "Dãy A", // Placeholder, as this info is not in the response
            checkInDate = this.checkinDate?.let { 
                try { isoFormatter.parse(it) } catch (e: Exception) { null }
            } ?: Date(),
            checkOutDate = this.checkoutDate?.let { 
                try { isoFormatter.parse(it) } catch (e: Exception) { null }
            } ?: Date(),
            status = this.status ?: "UNKNOWN",
            numGuests = this.numGuests ?: 1,
            note = this.note
        )
    }
}
