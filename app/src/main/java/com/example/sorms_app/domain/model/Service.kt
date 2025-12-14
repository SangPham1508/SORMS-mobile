package com.example.sorms_app.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.ui.graphics.vector.ImageVector

data class Service(
    val id: String,
    val name: String,
    // In a real app, the icon might be a URL or a string identifier.
    // For simplicity, we'll keep it as an ImageVector for now.
    val icon: ImageVector = Icons.Default.CleaningServices
)



