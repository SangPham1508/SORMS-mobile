package com.example.sorms_app.domain.repository

import com.example.sorms_app.presentation.components.StaffOption

interface StaffProfileRepository {
    suspend fun getActiveStaffOptions(): kotlin.Result<List<StaffOption>>
}

