package com.example.sorms_app.data.models

import com.google.gson.annotations.SerializedName

// Match backend BookingResponse
// Fields based on backend: id, code, userId, userName, roomId, roomCode, checkinDate, checkoutDate, numGuests, note, status, qrImageUrl

data class BookingResponse(
    @SerializedName("id") val id: Long?,
    @SerializedName("code") val code: String?,
    @SerializedName("userId") val userId: String?,
    @SerializedName("userName") val userName: String?,
    @SerializedName("roomId") val roomId: Long?,
    @SerializedName("roomCode") val roomCode: String?,
    @SerializedName("checkinDate") val checkinDate: String?,
    @SerializedName("checkoutDate") val checkoutDate: String?,
    @SerializedName("numGuests") val numGuests: Int?,
    @SerializedName("note") val note: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("qrImageUrl") val qrImageUrl: String?
)


