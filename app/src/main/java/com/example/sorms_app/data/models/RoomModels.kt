package com.example.sorms_app.data.models

import com.google.gson.annotations.SerializedName

// Match backend RoomResponse
// { id, code, name, roomTypeId, roomTypeCode, roomTypeName, floor, status, description, ... }
data class RoomResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("code") val code: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("roomTypeId") val roomTypeId: Long?,
    @SerializedName("roomTypeCode") val roomTypeCode: String?,
    @SerializedName("roomTypeName") val roomTypeName: String?,
    @SerializedName("floor") val floor: Int?,
    @SerializedName("status") val status: String?,
    @SerializedName("description") val description: String?
)

// UI model (giữ các field mới + field tương thích với code cũ)
data class RoomData(
    val id: Long,
    val code: String,
    val name: String,
    val roomTypeName: String,
    val floor: Int?,
    val status: String,
    val description: String?,
    // compatibility fields (để không lỗi khi code cũ đang dùng)
    val number: String,           // alias: code/name
    val isAvailable: Boolean,     // alias: status == AVAILABLE
    val type: String,             // alias: roomTypeName
    val capacity: String?         // backend chưa có -> để null/""
)

fun RoomResponse.toRoomData(): RoomData = RoomData(
    id = id,
    code = code ?: "RM-$id",
    name = name ?: code ?: "Room $id",
    roomTypeName = roomTypeName ?: roomTypeCode ?: "",
    floor = floor,
    status = status ?: "UNKNOWN",
    description = description,
    number = code ?: name ?: "RM-$id",
    isAvailable = (status ?: "").equals("AVAILABLE", ignoreCase = true),
    type = roomTypeName ?: roomTypeCode ?: "",
    capacity = null
)


