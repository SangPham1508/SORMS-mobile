package com.example.sorms_app.data.model

import com.google.gson.annotations.SerializedName

/**
 * Generic API Response wrapper
 */
data class ApiResponse<T>(
    @SerializedName("responseCode")
    val responseCode: String,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: T?
)

