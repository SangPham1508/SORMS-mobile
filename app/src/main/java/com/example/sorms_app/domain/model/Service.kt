package com.example.sorms_app.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Domain model for Service
 * Maps to backend ServiceResponse
 */
data class Service(
    val id: String,
    val code: String,
    val name: String,
    val description: String?,
    val unitPrice: Double,
    val unitName: String,
    val isActive: Boolean = true,
    // Icon for UI display
    val icon: ImageVector = Icons.Default.CleaningServices
)
