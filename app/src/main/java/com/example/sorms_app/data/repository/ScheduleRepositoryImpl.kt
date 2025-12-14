package com.example.sorms_app.data.repository

import com.example.sorms_app.data.datasource.remote.TaskApiService
import com.example.sorms_app.domain.model.Priority
import com.example.sorms_app.domain.model.Status
import com.example.sorms_app.domain.model.Task
import com.example.sorms_app.domain.repository.ScheduleRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val api: TaskApiService // Assuming schedule comes from task endpoints
) : ScheduleRepository {
    override fun getScheduledTasks(): Flow<Map<String, List<Task>>> = flow {
        // This is a mock implementation. A real implementation would fetch tasks
        // from the API and group them by date.
        delay(1000)
        val dateFormatter = SimpleDateFormat("EEEE, dd/MM/yyyy", Locale("vi", "VN"))
        
        val today = Date()
        val tomorrow = Date(System.currentTimeMillis() + 86400000)

        val schedule = mapOf(
            dateFormatter.format(today) to listOf(
                Task("1", "Dọn dẹp phòng 101", null, Priority.HIGH, Status.PENDING, today, "Lễ tân"),
                Task("2", "Sửa điều hòa phòng 203", null, Priority.MEDIUM, Status.IN_PROGRESS, today, "Lễ tân")
            ),
            dateFormatter.format(tomorrow) to listOf(
                Task("3", "Kiểm tra PCCC", null, Priority.LOW, Status.PENDING, tomorrow, "Ban quản lý")
            )
        )
        emit(schedule)
    }
}
