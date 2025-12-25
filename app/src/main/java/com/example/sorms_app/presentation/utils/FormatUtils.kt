package com.example.sorms_app.presentation.utils

import java.text.NumberFormat
import java.util.*

/**
 * Utility functions for formatting values
 */
object FormatUtils {
    /**
     * Format currency amount to Vietnamese format
     */
    fun formatCurrency(amount: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        return formatter.format(amount)
    }
}

