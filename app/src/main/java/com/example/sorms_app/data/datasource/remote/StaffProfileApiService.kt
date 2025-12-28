package com.example.sorms_app.data.datasource.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API Service for Staff Profiles
 * Mirrors web endpoint: /api/system/staff-profiles -> backend likely: GET /staff-profiles
 */
interface StaffProfileApiService {

    @GET("staff-profiles")
    suspend fun getStaffProfiles(
        @Query("status") status: String? = null,
        @Query("department") department: String? = null
    ): Response<ApiResponse<List<StaffProfileResponse>>>
}

data class StaffProfileResponse(
    @SerializedName("id") val id: Long?,
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("accountName") val accountName: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("department") val department: String?
)

