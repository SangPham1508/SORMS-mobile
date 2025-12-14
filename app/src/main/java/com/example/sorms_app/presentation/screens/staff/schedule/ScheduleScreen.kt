package com.example.sorms_app.presentation.screens.staff.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sorms_app.domain.model.Task
import com.example.sorms_app.presentation.components.SormsCard
import com.example.sorms_app.presentation.components.SormsEmptyState
import com.example.sorms_app.presentation.components.SormsLoading
import com.example.sorms_app.presentation.viewmodel.ScheduleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Lịch làm việc") })
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                SormsLoading(modifier = Modifier.padding(innerPadding))
            }
            uiState.errorMessage != null -> {
                SormsEmptyState(
                    title = "Lỗi",
                    subtitle = uiState.errorMessage ?: "Không thể tải lịch làm việc.",
                    modifier = Modifier.padding(innerPadding)
                )
            }
            uiState.schedule.isEmpty() -> {
                SormsEmptyState(
                    title = "Không có lịch trình",
                    subtitle = "Hiện tại bạn không có công việc nào được lên lịch.",
                    modifier = Modifier.padding(innerPadding)
                )
            }
            else -> {
                ScheduleContent(schedule = uiState.schedule, modifier = Modifier.padding(innerPadding))
            }
        }
    }
}

@Composable
private fun ScheduleContent(schedule: Map<String, List<Task>>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        schedule.forEach { (date, tasks) ->
            item {
                Text(
                    text = date,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            items(tasks) {
                ScheduleItem(task = it)
            }
        }
    }
}

@Composable
private fun ScheduleItem(task: Task) {
    SormsCard {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // In a real app, you'd format the time from the task's date object
            Text(text = "09:00", style = MaterialTheme.typography.bodyMedium)
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Column {
                Text(text = task.title, style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Phòng 101", // This should come from the task data
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
