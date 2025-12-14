package com.example.sorms_app.data.datasource.remote

import com.google.gson.annotations.SerializedName

// Generic API response wrapper to match backend
// { responseCode: string, message: string, data: T }

data class ApiResponse<T>(
    @SerializedName("responseCode") val responseCode: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: T? = null
)
