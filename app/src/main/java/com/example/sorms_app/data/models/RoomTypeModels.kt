package com.example.sorms_app.data.models

import com.google.gson.annotations.SerializedName

data class RoomTypeResponse(
    @SerializedName("id") val id: Long?,
    @SerializedName("code") val code: String?,
    @SerializedName("name") val name: String?
)


