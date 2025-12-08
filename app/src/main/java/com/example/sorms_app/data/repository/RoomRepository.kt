package com.example.sorms_app.data.repository

import android.util.Log
import com.example.sorms_app.data.api.RetrofitClient
import com.example.sorms_app.data.model.RoomData
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
     * Lấy tất cả phòng từ API
     * Thử cả 2 format: wrapped object và direct list
     */
    suspend fun getAllRooms(): Result<List<RoomData>> {
        return withContext(Dispatchers.IO) {
            try {
                // Thử lấy dữ liệu với format wrapped object trước
                val response = apiService.getAllRooms()
                
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        val rooms = body.getRoomsList().map { it.toRoomData() }
                        if (rooms.isNotEmpty()) {
                            Log.d(TAG, "Loaded ${rooms.size} rooms (wrapped format)")
                            return@withContext Result.Success(rooms)
                        }
                    }
                }
                
                // Nếu không thành công, thử format direct list
                val listResponse = apiService.getAllRoomsList()
                
                if (listResponse.isSuccessful) {
                    val body = listResponse.body()
                    if (body != null && body.isNotEmpty()) {
                        val rooms = body.map { it.toRoomData() }
                        Log.d(TAG, "Loaded ${rooms.size} rooms (list format)")
                        return@withContext Result.Success(rooms)
                    }
                }
                
                // Nếu cả 2 đều trả về rỗng thì trả về danh sách rỗng
                if (response.isSuccessful || listResponse.isSuccessful) {
                    Log.d(TAG, "API returned empty room list")
                    return@withContext Result.Success(emptyList())
                }
                
                if (response.code() == 401) {
                    return@withContext Result.Error("401 - Thiếu hoặc sai Authorization (API_TOKEN)")
                }
                if (listResponse.code() == 401) {
                    return@withContext Result.Error("401 - Thiếu hoặc sai Authorization (API_TOKEN)")
                }
                Result.Error("Lỗi server: ${response.code()} - ${response.message()}")
                
            } catch (e: Exception) {
                Log.e(TAG, "Error loading rooms", e)
                Result.Error("Lỗi kết nối: ${e.localizedMessage}", e)
            }
        }
    }
    
    /**
     * Lấy danh sách phòng còn trống
     */
    suspend fun getAvailableRooms(): Result<List<RoomData>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAvailableRooms()
                
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        val rooms = body.getRoomsList().map { it.toRoomData() }
                        Result.Success(rooms)
                    } else {
                        Result.Error("Không thể lấy dữ liệu phòng")
                    }
                } else {
                    Result.Error("Lỗi server: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading available rooms", e)
                Result.Error("Lỗi kết nối: ${e.localizedMessage}", e)
            }
        }
    }
    
    /**
     * Lấy thông tin chi tiết một phòng
     */
    suspend fun getRoomById(roomId: String): Result<RoomData> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getRoomById(roomId)
                
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        Result.Success(body.toRoomData())
                    } else {
                        Result.Error("Không tìm thấy phòng")
                    }
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






