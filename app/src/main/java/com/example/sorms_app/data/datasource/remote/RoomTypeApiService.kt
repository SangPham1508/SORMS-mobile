package com.example.sorms_app.data.datasource.remote

import com.example.sorms_app.data.model.RoomTypeResponse
import retrofit2.Response
import retrofit2.http.GET

interface RoomTypeApiService {
    // GET /room-types -> ApiResponse<List<RoomTypeResponse>>
    @GET("room-types")
    suspend fun getAll(): Response<ApiResponse<List<RoomTypeResponse>>>
}


