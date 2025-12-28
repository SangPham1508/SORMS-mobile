package com.example.sorms_app.data.repository

import com.example.sorms_app.data.datasource.remote.RetrofitClient
import com.example.sorms_app.domain.repository.StaffProfileRepository
import com.example.sorms_app.presentation.components.StaffOption
import javax.inject.Inject

class StaffProfileRepositoryImpl @Inject constructor() : StaffProfileRepository {

    override suspend fun getActiveStaffOptions(): kotlin.Result<List<StaffOption>> {
        return try {
            val response = RetrofitClient.staffProfileApiService.getStaffProfiles(status = "ACTIVE")
            if (response.isSuccessful) {
                val list = response.body()?.data ?: emptyList()
                val options = list.mapNotNull { sp ->
                    val id = sp.id ?: return@mapNotNull null
                    val name = sp.fullName ?: sp.name ?: sp.accountName ?: "Nhân viên #$id"
                    StaffOption(id = id, name = name)
                }
                kotlin.Result.success(options)
            } else {
                kotlin.Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            kotlin.Result.failure(e)
        }
    }
}

