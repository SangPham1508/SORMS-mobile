package com.example.sorms_app.data.datasource.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ServiceApiService {

    @GET("services")
    suspend fun getAvailableServices(): Response<ApiResponse<List<ServiceResponse>>>

    @POST("orders") // Assuming this is the endpoint for creating a service order
    suspend fun createServiceRequest(@Body request: ServiceRequest): Response<ApiResponse<Any>> // Assuming a generic response
}

data class ServiceResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("icon") val iconName: String?
)

data class ServiceRequest(
    @SerializedName("serviceId") val serviceId: String,
    @SerializedName("notes") val notes: String
)
