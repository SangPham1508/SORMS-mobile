package com.example.sorms_app.data.repository

import com.example.sorms_app.data.api.RetrofitClient
import com.example.sorms_app.data.model.BookingResponse
import com.example.sorms_app.data.model.CheckinResponse
import com.example.sorms_app.data.model.CreateBookingRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 * Repository for Booking operations
 * Provides a clean API for the UI layer to interact with booking data
 */
class BookingRepository {

    private val apiService = RetrofitClient.bookingApiService

    /**
     * Get all bookings for a specific user
     */
    suspend fun getBookingsByUser(userId: Long): Result<List<BookingResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getBookingsByUser(userId)
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.data != null) {
                        Result.success(apiResponse.data)
                    } else {
                        Result.success(emptyList())
                    }
                } else {
                    Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Get a specific booking by ID
     */
    suspend fun getBookingById(bookingId: Long): Result<BookingResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getBookingById(bookingId)
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.data != null) {
                        Result.success(apiResponse.data)
                    } else {
                        Result.failure(Exception("Booking not found"))
                    }
                } else {
                    Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Create a new booking
     */
    suspend fun createBooking(
        code: String,
        userId: String,
        roomId: Long,
        checkinDate: String,
        checkoutDate: String,
        numGuests: Int,
        note: String?
    ): Result<BookingResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = CreateBookingRequest(
                    code = code,
                    userId = userId,
                    roomId = roomId,
                    checkinDate = checkinDate,
                    checkoutDate = checkoutDate,
                    numGuests = numGuests,
                    note = note
                )
                
                val response = apiService.createBooking(request)
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.data != null) {
                        Result.success(apiResponse.data)
                    } else {
                        Result.failure(Exception("Failed to create booking"))
                    }
                } else {
                    Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Check-in to a booking
     */
    suspend fun checkinBooking(
        bookingId: Long,
        userId: String,
        faceImageFile: File? = null
    ): Result<CheckinResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val faceImagePart = faceImageFile?.let { file ->
                    val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("face_image", file.name, requestBody)
                }
                
                val response = apiService.checkinBooking(bookingId, userId, faceImagePart)
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.data != null) {
                        Result.success(apiResponse.data)
                    } else {
                        Result.failure(Exception("Failed to check-in"))
                    }
                } else {
                    Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Get all bookings (admin/staff only)
     */
    suspend fun getAllBookings(): Result<List<BookingResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAllBookings()
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.data != null) {
                        Result.success(apiResponse.data)
                    } else {
                        Result.success(emptyList())
                    }
                } else {
                    Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    companion object {
        @Volatile
        private var instance: BookingRepository? = null

        fun getInstance(): BookingRepository {
            return instance ?: synchronized(this) {
                instance ?: BookingRepository().also { instance = it }
            }
        }
    }
}

