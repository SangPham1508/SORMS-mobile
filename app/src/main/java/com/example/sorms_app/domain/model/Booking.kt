package com.example.sorms_app.domain.model

import java.util.Date

data class Booking(
    val id: Long,  // Changed to Long for API compatibility
    val code: String,  // Booking code (e.g., "BK-2024-001")
    val roomName: String,
    val roomId: Long? = null,
    val buildingName: String,
    val checkInDate: Date,
    val checkOutDate: Date,
    val status: String, // e.g., "PENDING", "APPROVED", "CHECKED_IN", "CHECKED_OUT", "CANCELLED"
    val numberOfGuests: Int = 1,  // Match HistoryScreen expectation
    val numGuests: Int = 1,       // Keep for backward compatibility
    val notes: String? = null,    // Match HistoryScreen expectation
    val note: String? = null      // Keep for backward compatibility
) {
    // Helper property to get string ID for backward compatibility
    val stringId: String get() = id.toString()
}
