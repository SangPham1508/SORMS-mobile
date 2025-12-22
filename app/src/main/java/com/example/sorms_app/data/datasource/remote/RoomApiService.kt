package com.example.sorms_app.data.datasource.remote

import com.example.sorms_app.data.models.RoomResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * API Service phòng - khớp với backend RoomController
 * Base: /rooms
 */
interface RoomApiService {

    // GET /rooms -> ApiResponse<List<RoomResponse>>
    @GET("rooms")
    suspend fun getAllRooms(): Response<ApiResponse<List<RoomResponse>>>

    // GET /rooms/{id} -> ApiResponse<RoomResponse>
    @GET("rooms/{id}")
    suspend fun getRoomById(@Path("id") id: Long): Response<ApiResponse<RoomResponse>>

    // GET /rooms/by-room-type/{roomTypeId} -> ApiResponse<List<RoomResponse>>
    @GET("rooms/by-room-type/{roomTypeId}")
    suspend fun getRoomsByRoomType(@Path("roomTypeId") roomTypeId: Long): Response<ApiResponse<List<RoomResponse>>>

    // GET /rooms/by-status/{status}?startTime=...&endTime=...
    @GET("rooms/by-status/{status}")
    suspend fun getRoomsByStatus(
        @Path("status") status: String,
        @Query("startTime") startTimeIso: String? = null,
        @Query("endTime") endTimeIso: String? = null
    ): Response<ApiResponse<List<RoomResponse>>>
}


