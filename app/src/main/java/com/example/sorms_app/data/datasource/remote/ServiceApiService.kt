package com.example.sorms_app.data.datasource.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.POST

/**
 * API Service for Services
 * Maps to backend ServiceController endpoints
 */
interface ServiceApiService {

    // GET /services - Get all available services
    @GET("services")
    suspend fun getAvailableServices(): Response<ApiResponse<List<ServiceResponse>>>

    // GET /services/{id} - Get service by ID
    @GET("services/{id}")
    suspend fun getServiceById(@Path("id") id: Long): Response<ApiResponse<ServiceResponse>>
}

/**
 * Service response matching backend ServiceResponse
 * Backend fields: id, code, name, description, unitPrice, unitName, isActive, createdDate, lastModifiedDate
 */
data class ServiceResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("code") val code: String?,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("unitPrice") val unitPrice: Double?,
    @SerializedName("unitName") val unitName: String?,
    @SerializedName("isActive") val isActive: Boolean?,
    @SerializedName("createdDate") val createdDate: String?,
    @SerializedName("lastModifiedDate") val lastModifiedDate: String?,
    // Legacy field for icon mapping
    @SerializedName("icon") val iconName: String?
)
