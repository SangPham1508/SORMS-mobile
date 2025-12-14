package com.example.sorms_app.domain.model

import java.util.Date

data class Booking(
    val id: String,
    val roomName: String,
    val buildingName: String,
    val checkInDate: Date,
    val checkOutDate: Date,
    val status: String // e.g., "CHECKED_IN", "CONFIRMED", "COMPLETED"
)
