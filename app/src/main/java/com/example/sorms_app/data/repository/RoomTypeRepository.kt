package com.example.sorms_app.data.repository

import com.example.sorms_app.data.api.RetrofitClient
import com.example.sorms_app.data.model.RoomTypeResponse

sealed class RoomTypeResult {
    data class Success(val data: List<RoomTypeResponse>) : RoomTypeResult()
    data class Error(val message: String) : RoomTypeResult()
}

class RoomTypeRepository {
    private val api = RetrofitClient.roomTypeApiService

    suspend fun getAll(): RoomTypeResult {
        return try {
            val res = api.getAll()
            if (res.isSuccessful) {
                RoomTypeResult.Success(res.body()?.data ?: emptyList())
            } else {
                RoomTypeResult.Error("${res.code()} - ${res.message()}")
            }
        } catch (e: Exception) {
            RoomTypeResult.Error(e.localizedMessage ?: "Unknown error")
        }
    }
}

