package com.example.sorms_app.data.datasource.remote

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * API Service for User Profile and Verification
 */
interface UserApiService {

    // GET /users/profile - Get current user profile (assumed endpoint)
    @GET("users/{userId}")
    suspend fun getUserProfile(@Path("userId") userId: String): Response<ApiResponse<UserProfileResponse>>

    // PUT /users/profile - Update user profile
    @PUT("users/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<ApiResponse<UserProfileResponse>>

    // POST /ai/recognition/face/register - Register face for verification
    @Multipart
    @POST("ai/recognition/face/register")
    suspend fun registerFace(
        @Part("userId") userId: RequestBody,
        @Part("name") name: RequestBody,
        @Part faceImages: List<MultipartBody.Part>
    ): Response<ApiResponse<FaceRegistrationResponse>>
}

// ==================== Request Models ====================

data class UpdateProfileRequest(
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("phoneNumber") val phoneNumber: String?,
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("dateOfBirth") val dateOfBirth: String?, // yyyy-MM-dd
    @SerializedName("gender") val gender: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("city") val city: String?,
    @SerializedName("state") val state: String?,
    @SerializedName("postalCode") val postalCode: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("avatarUrl") val avatarUrl: String?,
    @SerializedName("emergencyContactName") val emergencyContactName: String?,
    @SerializedName("emergencyContactPhone") val emergencyContactPhone: String?,
    @SerializedName("emergencyContactRelationship") val emergencyContactRelationship: String?,
    // ID Card (CCCD) information
    @SerializedName("idCardNumber") val idCardNumber: String?,
    @SerializedName("idCardIssueDate") val idCardIssueDate: String?, // yyyy-MM-dd
    @SerializedName("idCardIssuePlace") val idCardIssuePlace: String?
)

// ==================== Response Models ====================

data class UserProfileResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("phoneNumber") val phoneNumber: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("dateOfBirth") val dateOfBirth: String?,
    @SerializedName("gender") val gender: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("city") val city: String?,
    @SerializedName("state") val state: String?,
    @SerializedName("postalCode") val postalCode: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("avatarUrl") val avatarUrl: String?,
    @SerializedName("bio") val bio: String?,
    @SerializedName("emergencyContactName") val emergencyContactName: String?,
    @SerializedName("emergencyContactPhone") val emergencyContactPhone: String?,
    @SerializedName("emergencyContactRelationship") val emergencyContactRelationship: String?,
    // ID Card (CCCD) information
    @SerializedName("idCardNumber") val idCardNumber: String?,
    @SerializedName("idCardIssueDate") val idCardIssueDate: String?,
    @SerializedName("idCardIssuePlace") val idCardIssuePlace: String?,
    // Metadata
    @SerializedName("createdDate") val createdDate: String?,
    @SerializedName("lastModifiedDate") val lastModifiedDate: String?
)

data class FaceRegistrationResponse(
    @SerializedName("success") val success: Boolean?,
    @SerializedName("message") val message: String?,
    @SerializedName("userId") val userId: String?
)

