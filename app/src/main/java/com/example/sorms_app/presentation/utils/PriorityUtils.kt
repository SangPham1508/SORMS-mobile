package com.example.sorms_app.presentation.utils

import com.example.sorms_app.domain.model.Priority
import com.example.sorms_app.presentation.components.BadgeTone

/**
 * Utility functions for priority formatting and badge tones
 */
object PriorityUtils {
    /**
     * Get display text for task priority
     */
    fun getPriorityText(priority: Priority?): String {
        return when (priority) {
            Priority.LOW -> "Thấp"
            Priority.MEDIUM -> "Trung bình"
            Priority.HIGH -> "Cao"
            null -> "Bình thường"
        }
    }

    /**
     * Get badge tone for task priority
     */
    fun getPriorityBadgeTone(priority: Priority?): BadgeTone {
        return when (priority) {
            Priority.LOW -> BadgeTone.Default
            Priority.MEDIUM -> BadgeTone.Warning
            Priority.HIGH -> BadgeTone.Error
            null -> BadgeTone.Default
        }
    }

    /**
     * Get display text for priority string (for compatibility)
     */
    fun getPriorityText(priorityString: String?): String {
        return when (priorityString?.uppercase()) {
            "HIGH" -> "Cao"
            "MEDIUM" -> "Trung bình"
            "LOW" -> "Thấp"
            else -> "Bình thường"
        }
    }
}

