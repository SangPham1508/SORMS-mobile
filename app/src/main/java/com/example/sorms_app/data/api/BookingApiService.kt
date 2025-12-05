package com.example.sorms_app.data.api

import com.example.sorms_app.data.model.ApiResponse
import com.example.sorms_app.data.model.BookingResponse
import com.example.sorms_app.data.model.CheckinResponse
import com.example.sorms_app.data.model.CreateBookingRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API Service for Booking endpoints
 * Base URL: https://backend.sorms.online/api/
 */
interface BookingApiService {

    /**
     * Get all bookings for a specific user
     * GET /bookings/by-user/{userId}
     */
    @GET("bookings/by-user/{userId}")
    suspend fun getBookingsByUser(
        @Path("userId") userId: Long
    ): Response<ApiResponse<List<BookingResponse>>>

    /**
     * Get a specific booking by ID
     * GET /bookings/{id}
     */
    @GET("bookings/{id}")
    suspend fun getBookingById(
        @Path("id") bookingId: Long
    ): Response<ApiResponse<BookingResponse>>

    /**
     * Create a new booking
     * POST /bookings
     */
    @POST("bookings")
    suspend fun createBooking(
        @Body request: CreateBookingRequest
    ): Response<ApiResponse<BookingResponse>>

    /**
     * Check-in to a booking with face image
     * POST /bookings/{id}/checkin
     */
    @Multipart
    @POST("bookings/{id}/checkin")
    suspend fun checkinBooking(
        @Path("id") bookingId: Long,
        @Query("user_id") userId: String,
        @Part faceImage: MultipartBody.Part?
    ): Response<ApiResponse<CheckinResponse>>

    /**
     * Get all bookings (for admin/staff)
     * GET /bookings
     */
    @GET("bookings")
    suspend fun getAllBookings(): Response<ApiResponse<List<BookingResponse>>>
}

