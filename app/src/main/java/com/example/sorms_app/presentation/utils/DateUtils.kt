package com.example.sorms_app.presentation.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility functions for date formatting
 */
object DateUtils {
    private val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val dateShortFormatter = SimpleDateFormat("dd/MM", Locale.getDefault())
    private val dateTimeDisplayFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    private val dateTimeShortFormatter = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())

    /**
     * Format date string (ISO format) to display format
     */
    fun formatDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return "N/A"
        return try {
            val date = dateTimeFormatter.parse(dateString)
            date?.let { dateFormatter.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }

    /**
     * Format date string (ISO format) to date-time display format
     */
    fun formatDateTime(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return "N/A"
        return try {
            val date = dateTimeFormatter.parse(dateString)
            date?.let { dateTimeDisplayFormatter.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }

    /**
     * Format date string (ISO format) to short date-time format
     */
    fun formatDateTimeShort(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return "N/A"
        return try {
            val date = dateTimeFormatter.parse(dateString)
            date?.let { dateTimeShortFormatter.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }

    /**
     * Format Date object to display format
     */
    fun formatDate(date: Date?): String {
        if (date == null) return "N/A"
        return dateFormatter.format(date)
    }

    /**
     * Format Date object to date-time display format
     */
    fun formatDateTime(date: Date?): String {
        if (date == null) return "N/A"
        return dateTimeDisplayFormatter.format(date)
    }

    /**
     * Format Date object to short date format (dd/MM)
     */
    fun formatDateShort(date: Date?): String {
        if (date == null) return "N/A"
        return dateShortFormatter.format(date)
    }

    /**
     * Format Date object to ISO format (yyyy-MM-dd'T'HH:mm:ss) for API
     */
    fun formatDateToISO(date: Date?): String {
        if (date == null) return ""
        return dateTimeFormatter.format(date)
    }

    /**
     * Check if a date string (ISO format) is overdue (before current date)
     */
    fun isOverdue(dateString: String?): Boolean {
        if (dateString.isNullOrEmpty()) return false
        return try {
            val date = dateTimeFormatter.parse(dateString)
            date?.before(Date()) == true
        } catch (e: Exception) {
            false
        }
    }
}

