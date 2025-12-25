package com.example.sorms_app.presentation.utils

import com.example.sorms_app.presentation.components.BadgeTone

/**
 * Utility functions for status formatting and badge tones
 */
object StatusUtils {
    /**
     * Get display text for booking status
     */
    fun getBookingStatusText(status: String): String {
        return when (status.uppercase()) {
            "PENDING" -> "Chờ duyệt"
            "APPROVED" -> "Đã duyệt"
            "CHECKED_IN" -> "Đã check-in"
            "CHECKED_OUT" -> "Đã check-out"
            "CANCELLED" -> "Đã hủy"
            "REJECTED" -> "Từ chối"
            else -> status
        }
    }

    /**
     * Get badge tone for booking status
     */
    fun getBookingStatusBadgeTone(status: String): BadgeTone {
        return when (status.uppercase()) {
            "CHECKED_IN", "APPROVED" -> BadgeTone.Success
            "PENDING" -> BadgeTone.Warning
            "CANCELLED", "REJECTED" -> BadgeTone.Error
            else -> BadgeTone.Default
        }
    }

    /**
     * Get display text for task status
     */
    fun getTaskStatusText(status: String): String {
        return when (status.uppercase()) {
            "PENDING" -> "Chờ xử lý"
            "IN_PROGRESS" -> "Đang thực hiện"
            "COMPLETED" -> "Hoàn thành"
            "CANCELLED", "REJECTED" -> "Đã hủy"
            else -> status
        }
    }

    /**
     * Get badge tone for task status
     */
    fun getTaskStatusBadgeTone(status: String): BadgeTone {
        return when (status.uppercase()) {
            "COMPLETED" -> BadgeTone.Success
            "IN_PROGRESS" -> BadgeTone.Default
            "PENDING" -> BadgeTone.Warning
            "CANCELLED", "REJECTED" -> BadgeTone.Error
            else -> BadgeTone.Default
        }
    }

    /**
     * Get display text for room status
     */
    fun getRoomStatusText(status: String): String {
        return when (status.uppercase()) {
            "AVAILABLE" -> "Khả dụng"
            "OCCUPIED" -> "Đang ở"
            "MAINTENANCE" -> "Bảo trì"
            "CLEANING" -> "Dọn dẹp"
            "OUT_OF_SERVICE" -> "Ngừng hoạt động"
            else -> status
        }
    }

    /**
     * Get badge tone for room status
     */
    fun getRoomStatusBadgeTone(status: String): BadgeTone {
        return when (status.uppercase()) {
            "AVAILABLE" -> BadgeTone.Success
            "OCCUPIED" -> BadgeTone.Error
            "MAINTENANCE", "CLEANING" -> BadgeTone.Warning
            "OUT_OF_SERVICE" -> BadgeTone.Error
            else -> BadgeTone.Default
        }
    }

    /**
     * Get display text for service order status
     */
    fun getServiceOrderStatusText(status: String): String {
        return when (status.uppercase()) {
            "PENDING" -> "Chờ xử lý"
            "CONFIRMED" -> "Đã xác nhận"
            "IN_PROGRESS" -> "Đang thực hiện"
            "COMPLETED" -> "Hoàn thành"
            "CANCELLED" -> "Đã hủy"
            else -> status
        }
    }

    /**
     * Get badge tone for service order status
     */
    fun getServiceOrderStatusBadgeTone(status: String): BadgeTone {
        return when (status.uppercase()) {
            "COMPLETED" -> BadgeTone.Success
            "PENDING" -> BadgeTone.Warning
            "CONFIRMED", "IN_PROGRESS" -> BadgeTone.Default
            "CANCELLED" -> BadgeTone.Error
            else -> BadgeTone.Default
        }
    }
}

