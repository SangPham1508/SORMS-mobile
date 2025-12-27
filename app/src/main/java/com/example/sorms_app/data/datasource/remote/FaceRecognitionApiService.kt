package com.example.sorms_app.data.datasource.remote

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * API Service for AI Face Recognition
 * Maps to backend AIRecognitionController endpoints
 */
interface FaceRecognitionApiService {

    // POST /ai/recognition/face/register - Register user face images
    @Multipart
    @POST("ai/recognition/face/register")
    suspend fun registerFace(
        @Part("student_id") userId: RequestBody,
        @Part("images") images: List<MultipartBody.Part>
    ): Response<ApiResponse<FaceRegistrationResponse>>

    // GET /ai/recognition/faces/{id} - Get user face status by ID
    @GET("ai/recognition/faces/{id}")
    suspend fun getFaceStatus(@Path("id") userId: String): Response<ApiResponse<FaceStatusResponse>>

    // PUT /ai/recognition/faces/{id} - Update user face images
    @Multipart
    @PUT("ai/recognition/faces/{id}")
    suspend fun updateFace(
        @Path("id") userId: String,
        @Part("images") images: List<MultipartBody.Part>
    ): Response<ApiResponse<FaceRegistrationResponse>>

    // DELETE /ai/recognition/faces/{id} - Delete user face data
    @DELETE("ai/recognition/faces/{id}")
    suspend fun deleteFace(@Path("id") userId: String): Response<ApiResponse<Void>>

    // GET /ai/recognition/faces - Get all users (optional, for admin)
    @GET("ai/recognition/faces")
    suspend fun getAllFaces(): Response<ApiResponse<List<FaceStatusResponse>>>
}

// ==================== Response Models ====================

data class FaceRegistrationResponse(
    @SerializedName("success") val success: Boolean?,
    @SerializedName("message") val message: String?,
    @SerializedName("userId") val userId: String?
)

data class FaceStatusResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("registered") val registered: Boolean?,
    @SerializedName("message") val message: String?,
    // Add other fields based on backend response structure
    @SerializedName("createdDate") val createdDate: String?,
    @SerializedName("lastModifiedDate") val lastModifiedDate: String?
)

