package com.example.sorms_app.domain.model

import java.util.Date

data class Notification(
    val id: String,
    val message: String,
    val date: Date,
    val isRead: Boolean
)
