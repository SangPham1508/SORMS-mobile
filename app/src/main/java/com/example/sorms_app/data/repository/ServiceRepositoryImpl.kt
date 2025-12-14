package com.example.sorms_app.data.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.sorms_app.data.datasource.remote.ServiceApiService
import com.example.sorms_app.data.datasource.remote.ServiceRequest
import com.example.sorms_app.data.datasource.remote.ServiceResponse
import com.example.sorms_app.domain.model.Service
import com.example.sorms_app.domain.repository.ServiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ServiceRepositoryImpl @Inject constructor(
    private val api: ServiceApiService
) : ServiceRepository {

    override fun getAvailableServices(): Flow<List<Service>> = flow {
        try {
            val response = api.getAvailableServices()
            if (response.isSuccessful) {
                val serviceResponses = response.body()?.data ?: emptyList()
                val services = serviceResponses.map { it.toDomainModel() }
                emit(services)
            } else {
                throw Exception("Failed to fetch services: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            throw Exception("Network error fetching services: ${e.message}")
        }
    }

    override suspend fun createServiceRequest(serviceId: String, notes: String) {
        try {
            val request = ServiceRequest(serviceId = serviceId, notes = notes)
            val response = api.createServiceRequest(request)
            if (!response.isSuccessful) {
                throw Exception("Failed to create service request: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            throw Exception("Network error creating service request: ${e.message}")
        }
    }

    private fun ServiceResponse.toDomainModel(): Service {
        return Service(
            id = this.id,
            name = this.name,
            icon = mapIcon(this.iconName)
        )
    }

    private fun mapIcon(iconName: String?): ImageVector {
        return when (iconName?.lowercase()) {
            "cleaning" -> Icons.Default.CleaningServices
            "laundry" -> Icons.Default.LocalLaundryService
            "food" -> Icons.Default.Restaurant
            "repair" -> Icons.Default.Report
            "towel" -> Icons.Default.DryCleaning
            "internet" -> Icons.Default.Wifi
            else -> Icons.Default.Build // Default icon
        }
    }
}
