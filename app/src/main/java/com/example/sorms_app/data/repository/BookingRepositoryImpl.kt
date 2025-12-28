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
        // Đồng bộ với web: dùng /bookings endpoint thay vì /bookings/by-user/{userId}
        // Web dùng: /api/system/bookings (qua Next.js API route)
        // Mobile dùng: /bookings (trực tiếp backend, backend sẽ filter theo user từ token)
        try {
            val response = api.getAllBookings()
            if (response.isSuccessful) {
                val apiResponse = response.body()
                // Đồng bộ với web: parse từ nhiều formats
                // Web parse: bookings.items || bookings.data?.items || bookings.data?.content || bookings (array)
                val bookingsList: List<BookingResponse> = when {
                    // Case 1: data là List trực tiếp
                    apiResponse?.data is List<*> -> {
                        @Suppress("UNCHECKED_CAST")
                        (apiResponse.data as? List<BookingResponse>) ?: emptyList()
                    }
                    // Case 2: data có items (paginated response)
                    apiResponse?.data is Map<*, *> -> {
                        val dataMap = apiResponse.data as Map<*, *>
                        @Suppress("UNCHECKED_CAST")
                        when {
                            dataMap["items"] is List<*> -> (dataMap["items"] as? List<BookingResponse>) ?: emptyList()
                            dataMap["content"] is List<*> -> (dataMap["content"] as? List<BookingResponse>) ?: emptyList()
                            else -> emptyList()
                        }
                    }
                    else -> emptyList()
                }
                android.util.Log.d("BookingRepository", "Fetched ${bookingsList.size} bookings")
                emit(bookingsList.map { it.toDomainModel() })
            } else {
                val errorBody = response.errorBody()?.string()
                android.util.Log.e("BookingRepository", "API Error: ${response.code()} - ${response.message()}. Body: $errorBody")
                throw Exception("Failed to fetch bookings: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            android.util.Log.e("BookingRepository", "Error fetching bookings", e)
            // Emit empty list instead of throwing to prevent UI crash
            emit(emptyList())
        }
    }

    override fun getUserBookings(): Flow<List<Booking>> = getAllBookings()

    // Đồng bộ với web: implement createBooking
    override suspend fun createBooking(
        roomId: Long,
        checkinDate: String,
        checkoutDate: String,
        numGuests: Int,
        note: String?
    ): kotlin.Result<Booking> {
        return try {
            val userId = AuthSession.accountId
            if (userId == null) {
                return kotlin.Result.failure(Exception("User ID không tồn tại. Vui lòng đăng nhập lại."))
            }

            // Format datetime giống web: YYYY-MM-DDTHH:mm:ss
            val formattedCheckinDate = formatDateTime(checkinDate)
            val formattedCheckoutDate = formatDateTime(checkoutDate)

            val request = com.example.sorms_app.data.datasource.remote.CreateBookingRequest(
                code = null, // Backend sẽ tự generate
                userId = userId,
                roomId = roomId,
                checkinDate = formattedCheckinDate,
                checkoutDate = formattedCheckoutDate,
                numGuests = numGuests,
                note = note
            )

            val response = api.createBooking(request)
            if (response.isSuccessful) {
                val bookingResponse = response.body()?.data
                if (bookingResponse != null) {
                    kotlin.Result.success(bookingResponse.toDomainModel())
                } else {
                    kotlin.Result.failure(Exception("Không nhận được dữ liệu booking từ server"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = errorBody ?: "Đặt phòng thất bại: ${response.code()} - ${response.message()}"
                android.util.Log.e("BookingRepository", "Create booking error: $errorMessage")
                kotlin.Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            android.util.Log.e("BookingRepository", "Error creating booking", e)
            kotlin.Result.failure(e)
        }
    }

    // Đồng bộ với web: implement cancelBooking
    override suspend fun cancelBooking(bookingId: Long): kotlin.Result<Unit> {
        return try {
            val response = api.deleteBooking(bookingId)
            if (response.isSuccessful) {
                kotlin.Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = errorBody ?: "Hủy booking thất bại: ${response.code()} - ${response.message()}"
                android.util.Log.e("BookingRepository", "Cancel booking error: $errorMessage")
                kotlin.Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            android.util.Log.e("BookingRepository", "Error canceling booking", e)
            kotlin.Result.failure(e)
        }
    }

    // Đồng bộ với web: implement updateBooking
    override suspend fun updateBooking(
        bookingId: Long,
        checkinDate: String?,
        checkoutDate: String?,
        numGuests: Int?,
        note: String?
    ): kotlin.Result<Booking> {
        return try {
            val request = com.example.sorms_app.data.datasource.remote.UpdateBookingRequest(
                id = bookingId,
                checkinDate = checkinDate?.let { formatDateTime(it) },
                checkoutDate = checkoutDate?.let { formatDateTime(it) },
                numGuests = numGuests,
                note = note
            )

            val response = api.updateBooking(bookingId, request)
            if (response.isSuccessful) {
                val bookingResponse = response.body()?.data
                if (bookingResponse != null) {
                    kotlin.Result.success(bookingResponse.toDomainModel())
                } else {
                    kotlin.Result.failure(Exception("Không nhận được dữ liệu booking từ server"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = errorBody ?: "Cập nhật booking thất bại: ${response.code()} - ${response.message()}"
                android.util.Log.e("BookingRepository", "Update booking error: $errorMessage")
                kotlin.Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            android.util.Log.e("BookingRepository", "Error updating booking", e)
            kotlin.Result.failure(e)
        }
    }

    // Helper function để format datetime giống web
    private fun formatDateTime(dateTimeStr: String): String {
        // Nếu chỉ có ngày (YYYY-MM-DD), thêm thời gian mặc định 00:00:00
        if (dateTimeStr.matches(Regex("^\\d{4}-\\d{2}-\\d{2}$"))) {
            return "${dateTimeStr}T00:00:00"
        }
        
        // Nếu đã là format đúng (YYYY-MM-DDTHH:mm:ss), giữ nguyên
        if (dateTimeStr.matches(Regex("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$"))) {
            return dateTimeStr
        }
        
        // Nếu thiếu seconds (YYYY-MM-DDTHH:mm), thêm :00
        if (dateTimeStr.matches(Regex("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}$"))) {
            return "${dateTimeStr}:00"
        }
        
        // Nếu có timezone, bỏ timezone
        if (dateTimeStr.contains("+") || dateTimeStr.endsWith("Z")) {
            val withoutTz = dateTimeStr.replace(Regex("[+-]\\d{2}:\\d{2}$"), "").replace("Z", "")
            if (withoutTz.matches(Regex("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}$"))) {
                return "${withoutTz}:00"
            }
            return withoutTz
        }
        
        return dateTimeStr
    }

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
            note = this.note,
            qrImageUrl = this.qrImageUrl  // Map QR code URL từ backend (đồng bộ với web)
        )
    }
}
