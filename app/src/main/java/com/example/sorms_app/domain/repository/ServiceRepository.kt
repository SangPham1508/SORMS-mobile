package com.example.sorms_app.domain.repository

import com.example.sorms_app.domain.model.Service
import kotlinx.coroutines.flow.Flow

interface ServiceRepository {
    fun getAvailableServices(): Flow<List<Service>>
    suspend fun createServiceRequest(serviceId: String, notes: String)
}
