package com.example.sorms_app.data.datasource.remote

import com.example.sorms_app.data.models.BookingResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface BookingApiService {

    // POST /bookings
    @POST("bookings")
    suspend fun createBooking(@Body request: CreateBookingRequest): Response<ApiResponse<BookingResponse>>

    // PUT /bookings/{id}
    @PUT("bookings/{id}")
    suspend fun updateBooking(@Path("id") id: Long, @Body request: UpdateBookingRequest): Response<ApiResponse<BookingResponse>>

    // DELETE /bookings/{id}
    @DELETE("bookings/{id}")
    suspend fun deleteBooking(@Path("id") id: Long): Response<ApiResponse<Void>>

    // GET /bookings/{id}
    @GET("bookings/{id}")
    suspend fun getBookingById(@Path("id") id: Long): Response<ApiResponse<BookingResponse>>

    // GET /bookings
    @GET("bookings")
    suspend fun getAllBookings(): Response<ApiResponse<List<BookingResponse>>>

    // GET /bookings/by-status/{status}
    @GET("bookings/by-status/{status}")
    suspend fun getBookingsByStatus(@Path("status") status: String): Response<ApiResponse<List<BookingResponse>>>

    // GET /bookings/by-user/{userId}
    @GET("bookings/by-user/{userId}")
    suspend fun getBookingsByUser(@Path("userId") userId: String): Response<ApiResponse<List<BookingResponse>>>

    // POST /bookings/{id}/checkin (multipart)
    @Multipart
    @POST("bookings/{id}/checkin")
    suspend fun checkin(
        @Path("id") id: Long,
        @Part("userId") userId: RequestBody,
        @Part faceImage: MultipartBody.Part,
        @Part("faceRef") faceRef: RequestBody
    ): Response<ApiResponse<CheckinResponse>>

    // POST /bookings/{id}/checkout
    @POST("bookings/{id}/checkout")
    suspend fun checkout(@Path("id") id: Long, @Body request: CheckoutBookingRequest): Response<ApiResponse<CheckoutResponse>>
}

// Requests

data class CreateBookingRequest(
    val code: String?,
    val userId: String,
    val roomId: Long,
    val checkinDate: String, // ISO-8601
    val checkoutDate: String,
    val numGuests: Int?,
    val note: String?
)

data class UpdateBookingRequest(
    val id: Long,
    val checkinDate: String?,
    val checkoutDate: String?,
    val numGuests: Int?,
    val note: String?
)

data class CheckoutBookingRequest(
    val bookingId: Long,
    val userId: String
)

// Responses (minimal)

data class CheckinResponse(
    val success: Boolean?,
    val message: String?
)

data class CheckoutResponse(
    val success: Boolean?,
    val message: String?
)


