package com.example.sorms_app.data.repository

import android.util.Log
import com.example.sorms_app.data.api.RetrofitClient
import com.example.sorms_app.data.api.ApiResponse
import com.example.sorms_app.data.model.RoomData
import com.example.sorms_app.data.model.RoomResponse
import com.example.sorms_app.data.model.toRoomData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Sealed class để đại diện cho các trạng thái của kết quả API
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val exception: Exception? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

/**
 * Repository xử lý việc lấy dữ liệu phòng từ API
 * API: https://backend.sorms.online/api/
 */
class RoomRepository {
    
    private val apiService = RetrofitClient.roomApiService
    private val TAG = "RoomRepository"
    
    /**
     * Lấy tất cả phòng từ API (ApiResponse<List<RoomResponse>>)
     */
    suspend fun getAllRooms(): Result<List<RoomData>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAllRooms()
                if (response.isSuccessful) {
                    val rooms = response.body()?.data?.map { it.toRoomData() } ?: emptyList()
                    if (rooms.isNotEmpty()) {
                        Log.d(TAG, "Loaded ${rooms.size} rooms")
                        Result.Success(rooms)
                    } else {
                        // Fallback: thử lấy theo trạng thái AVAILABLE
                        val byStatus = apiService.getRoomsByStatus("AVAILABLE")
                        if (byStatus.isSuccessful) {
                            val list = byStatus.body()?.data?.map { it.toRoomData() } ?: emptyList()
                            Log.d(TAG, "Loaded ${list.size} rooms by status AVAILABLE")
                            Result.Success(list)
                        } else {
                            Result.Error("Lỗi server: ${byStatus.code()} - ${byStatus.message()}")
                        }
                    }
                } else {
                    Result.Error("Lỗi server: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading rooms", e)
                Result.Error("Lỗi kết nối: ${e.localizedMessage}", e)
            }
        }
    }

    /**
     * Lấy danh sách phòng theo trạng thái
     */
    suspend fun getRoomsByStatus(status: String): Result<List<RoomData>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getRoomsByStatus(status)
                if (response.isSuccessful) {
                    val rooms = response.body()?.data?.map { it.toRoomData() } ?: emptyList()
                    Result.Success(rooms)
                } else {
                    Result.Error("Lỗi server: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading rooms by status $status", e)
                Result.Error("Lỗi kết nối: ${e.localizedMessage}", e)
            }
        }
    }
    
    /**
     * Lấy danh sách phòng theo RoomType
     */
    suspend fun getRoomsByType(roomTypeId: Long): Result<List<RoomData>> {
        return withContext(Dispatchers.IO) {
            try {
                val res = apiService.getRoomsByRoomType(roomTypeId)
                if (res.isSuccessful) {
                    val rooms = res.body()?.data?.map { it.toRoomData() } ?: emptyList()
                    // Lọc AVAILABLE mặc định
                    val filtered = rooms.filter { it.status.equals("AVAILABLE", ignoreCase = true) }
                    Result.Success(filtered)
                } else {
                    Result.Error("Lỗi server: ${res.code()} - ${res.message()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading rooms by type $roomTypeId", e)
                Result.Error("Lỗi kết nối: ${e.localizedMessage}", e)
            }
        }
    }

    /**
     * Lấy thông tin chi tiết một phòng
     */
    suspend fun getRoomById(roomId: Long): Result<RoomData> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getRoomById(roomId)
                if (response.isSuccessful) {
                    val body = response.body()?.data
                    if (body != null) Result.Success(body.toRoomData()) else Result.Error("Không tìm thấy phòng")
                } else {
                    Result.Error("Lỗi server: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading room $roomId", e)
                Result.Error("Lỗi kết nối: ${e.localizedMessage}", e)
            }
        }
    }
}






