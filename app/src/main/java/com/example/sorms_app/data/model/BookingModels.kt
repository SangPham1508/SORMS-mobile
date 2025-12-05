package com.example.sorms_app.data.model

import com.google.gson.annotations.SerializedName

/**
 * Response model for Booking
 */
data class BookingResponse(
    @SerializedName("id")
    val id: Long,
    
    @SerializedName("code")
    val code: String,
    
    @SerializedName("userId")
    val userId: Long,
    
    @SerializedName("userName")
    val userName: String?,
    
    @SerializedName("roomId")
    val roomId: Long,
    
    @SerializedName("roomCode")
    val roomCode: String?,
    
    @SerializedName("checkinDate")
    val checkinDate: String,
    
    @SerializedName("checkoutDate")
    val checkoutDate: String,
    
    @SerializedName("numGuests")
    val numGuests: Int,
    
    @SerializedName("note")
    val note: String?,
    
    @SerializedName("status")
    val status: BookingStatus,
    
    @SerializedName("qrImageUrl")
    val qrImageUrl: String?
)

/**
 * Request model for creating a new Booking
 */
data class CreateBookingRequest(
    @SerializedName("code")
    val code: String,
    
    @SerializedName("userId")
    val userId: String,
    
    @SerializedName("roomId")
    val roomId: Long,
    
    @SerializedName("checkinDate")
    val checkinDate: String,
    
    @SerializedName("checkoutDate")
    val checkoutDate: String,
    
    @SerializedName("numGuests")
    val numGuests: Int,
    
    @SerializedName("note")
    val note: String?
)

/**
 * Response model for Check-in
 */
data class CheckinResponse(
    @SerializedName("id")
    val id: Long,
    
    @SerializedName("bookingId")
    val bookingId: Long,
    
    @SerializedName("userId")
    val userId: Long,
    
    @SerializedName("userName")
    val userName: String?,
    
    @SerializedName("roomId")
    val roomId: Long,
    
    @SerializedName("roomCode")
    val roomCode: String?,
    
    @SerializedName("faceRef")
    val faceRef: String?,
    
    @SerializedName("checkinAt")
    val checkinAt: String?,
    
    @SerializedName("checkoutAt")
    val checkoutAt: String?
)

/**
 * Booking status enum matching backend
 */
enum class BookingStatus {
    PENDING,
    APPROVED,
    REJECTED,
    CANCELLED,
    CHECKED_IN,
    CHECKED_OUT
}

