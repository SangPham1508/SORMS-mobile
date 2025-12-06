package com.example.sorms_app.data.api

import com.example.sorms_app.data.model.RoomResponse
import com.example.sorms_app.data.model.RoomsApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * API Service để lấy dữ liệu phòng
 * Xem Swagger UI: https://backend.sorms.online/api/swagger-ui/index.html
 */
interface RoomApiService {
    
    /**
     * Lấy danh sách tất cả phòng
     * GET /rooms
     */
    @GET("rooms")
    suspend fun getAllRooms(): Response<RoomsApiResponse>
    
    /**
     * Lấy danh sách phòng (response có thể là List trực tiếp)
     */
    @GET("rooms")
    suspend fun getAllRoomsList(): Response<List<RoomResponse>>
    
    /**
     * Lấy danh sách phòng còn trống
     * GET /rooms/available
     */
    @GET("rooms/available")
    suspend fun getAvailableRooms(): Response<RoomsApiResponse>
    
    /**
     * Lấy thông tin chi tiết phòng theo ID
     * GET /rooms/{id}
     */
    @GET("rooms/{id}")
    suspend fun getRoomById(@Path("id") roomId: String): Response<RoomResponse>
    
    /**
     * Lấy danh sách phòng theo tầng
     * GET /rooms?floor=3
     */
    @GET("rooms")
    suspend fun getRoomsByFloor(@Query("floor") floor: Int): Response<RoomsApiResponse>
    
    /**
     * Lấy danh sách phòng theo loại
     * GET /rooms?type=single
     */
    @GET("rooms")
    suspend fun getRoomsByType(@Query("type") type: String): Response<RoomsApiResponse>
}



