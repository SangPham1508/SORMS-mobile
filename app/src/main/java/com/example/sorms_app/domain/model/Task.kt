package com.example.sorms_app.domain.model

import java.util.Date

data class Task(
    val id: String,
    val title: String,
    val description: String?,
    val priority: Priority,
    val status: Status,
    val dueDate: Date?,
    val assignedBy: String?
)

enum class Priority {
    HIGH, MEDIUM, LOW
}

enum class Status {
    PENDING, IN_PROGRESS, COMPLETED, REJECTED
}


