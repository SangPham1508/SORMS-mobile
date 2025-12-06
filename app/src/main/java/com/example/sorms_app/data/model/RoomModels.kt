package com.example.sorms_app.data.model

import com.google.gson.annotations.SerializedName

/**
 * Model phòng từ API
 * Điều chỉnh các field theo response API thực tế từ https://backend.sorms.online/api/
 */
data class RoomResponse(
    @SerializedName("id")
    val id: String? = null,
    
    @SerializedName("roomId")
    val roomId: String? = null,
    
    @SerializedName("number")
    val number: String? = null,
    
    @SerializedName("roomNumber")
    val roomNumber: String? = null,
    
    @SerializedName("name")
    val name: String? = null,
    
    @SerializedName("type")
    val type: String? = null,
    
    @SerializedName("roomType")
    val roomType: String? = null,
    
    @SerializedName("is_available")
    val isAvailable: Boolean? = null,
    
    @SerializedName("isAvailable")
    val available: Boolean? = null,
    
    @SerializedName("status")
    val status: String? = null,
    
    @SerializedName("floor")
    val floor: String? = null,
    
    @SerializedName("floorNumber")
    val floorNumber: Int? = null,
    
    @SerializedName("capacity")
    val capacity: String? = null,
    
    @SerializedName("maxCapacity")
    val maxCapacity: Int? = null,
    
    @SerializedName("price")
    val price: Double? = null,
    
    @SerializedName("pricePerMonth")
    val pricePerMonth: Double? = null,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("amenities")
    val amenities: List<String>? = null,
    
    @SerializedName("building")
    val building: String? = null
)

/**
 * Wrapper cho response API danh sách phòng
 */
data class RoomsApiResponse(
    @SerializedName("success")
    val success: Boolean? = null,
    
    @SerializedName("status")
    val status: String? = null,
    
    @SerializedName("data")
    val data: List<RoomResponse>? = null,
    
    @SerializedName("rooms")
    val rooms: List<RoomResponse>? = null,
    
    @SerializedName("content")
    val content: List<RoomResponse>? = null,
    
    @SerializedName("message")
    val message: String? = null,
    
    @SerializedName("totalElements")
    val totalElements: Int? = null
) {
    /**
     * Lấy danh sách phòng từ bất kỳ field nào có data
     */
    fun getRoomsList(): List<RoomResponse> {
        return data ?: rooms ?: content ?: emptyList()
    }
}

/**
 * Model Room để sử dụng trong UI
 */
data class RoomData(
    val id: String,
    val number: String,
    val type: String,
    val isAvailable: Boolean,
    val floor: String,
    val capacity: String,
    val price: Double? = null,
    val description: String? = null,
    val amenities: List<String>? = null
)

/**
 * Extension function để convert từ API response sang UI model
 */
fun RoomResponse.toRoomData(): RoomData {
    // Xác định số phòng
    val roomNum = number ?: roomNumber ?: name ?: id ?: roomId ?: "N/A"
    
    // Xác định loại phòng
    val roomTypeStr = type ?: roomType ?: "Phòng"
    
    // Xác định trạng thái còn trống
    val isRoomAvailable = isAvailable ?: available ?: (status?.lowercase() == "available" || status?.lowercase() == "trống")
    
    // Xác định tầng
    val floorStr = floor ?: floorNumber?.let { "Tầng $it" } ?: ""
    
    // Xác định sức chứa
    val capacityStr = capacity ?: maxCapacity?.let { "$it người" } ?: ""
    
    return RoomData(
        id = id ?: roomId ?: roomNum,
        number = roomNum,
        type = roomTypeStr,
        isAvailable = isRoomAvailable,
        floor = floorStr,
        capacity = capacityStr,
        price = price ?: pricePerMonth,
        description = description,
        amenities = amenities
    )
}



