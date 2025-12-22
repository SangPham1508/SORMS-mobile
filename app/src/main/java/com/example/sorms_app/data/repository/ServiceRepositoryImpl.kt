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
                // Filter only active services
                val services = serviceResponses
                    .filter { it.isActive != false }
                    .map { it.toDomainModel() }
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
            id = this.id.toString(),
            code = this.code ?: "SVC-${this.id}",
            name = this.name,
            description = this.description,
            unitPrice = this.unitPrice ?: 0.0,
            unitName = this.unitName ?: "lần",
            isActive = this.isActive ?: true,
            icon = mapIcon(this.iconName ?: this.code)
        )
    }

    private fun mapIcon(identifier: String?): ImageVector {
        return when (identifier?.lowercase()) {
            "cleaning", "clean", "don_phong" -> Icons.Default.CleaningServices
            "laundry", "giat_ui", "giat" -> Icons.Default.LocalLaundryService
            "food", "an_uong", "restaurant" -> Icons.Default.Restaurant
            "repair", "sua_chua", "maintenance" -> Icons.Default.Build
            "towel", "khan", "drycleaning" -> Icons.Default.DryCleaning
            "internet", "wifi", "mang" -> Icons.Default.Wifi
            "parking", "dau_xe", "xe" -> Icons.Default.LocalParking
            "gym", "tap_gym", "fitness" -> Icons.Default.FitnessCenter
            "pool", "ho_boi", "swim" -> Icons.Default.Pool
            "spa", "massage" -> Icons.Default.Spa
            "medical", "y_te", "health" -> Icons.Default.MedicalServices
            "security", "bao_ve", "an_ninh" -> Icons.Default.Security
            else -> Icons.Default.MiscellaneousServices
        }
    }
}
