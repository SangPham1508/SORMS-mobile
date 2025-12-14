package com.example.sorms_app.presentation.screens.staff.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sorms_app.domain.model.Priority
import com.example.sorms_app.domain.model.Status
import com.example.sorms_app.domain.model.Task
import com.example.sorms_app.presentation.components.*
import com.example.sorms_app.presentation.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StaffDashboardScreen(
    taskViewModel: TaskViewModel = hiltViewModel(),
    onTaskClick: (String) -> Unit
) {
    val uiState by taskViewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> {
            SormsLoading()
        }
        uiState.errorMessage != null -> {
            SormsEmptyState(
                title = "Đã xảy ra lỗi",
                subtitle = uiState.errorMessage ?: "Không thể tải dữ liệu. Vui lòng thử lại."
            )
        }
        else -> {
            TaskListScreen(
                tasks = uiState.tasks,
                onTaskClick = onTaskClick
            )
        }
    }
}

@Composable
private fun TaskListScreen(
    tasks: List<Task>,
    onTaskClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(text = "Công việc của bạn", style = MaterialTheme.typography.titleLarge)
        }

        item {
            StatsRow(tasks = tasks)
        }

        if (tasks.isEmpty()) {
            item {
                SormsEmptyState(
                    title = "Không có công việc",
                    subtitle = "Hiện tại bạn không có công việc nào được giao."
                )
            }
        } else {
            items(tasks) { task ->
                TaskItem(task = task, onTaskClick = onTaskClick)
            }
        }
    }
}

@Composable
private fun StatsRow(tasks: List<Task>) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        StatCard(
            label = "Chờ nhận",
            count = tasks.count { it.status == Status.PENDING },
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Đang làm",
            count = tasks.count { it.status == Status.IN_PROGRESS },
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Hoàn thành",
            count = tasks.count { it.status == Status.COMPLETED },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(label: String, count: Int, modifier: Modifier = Modifier) {
    SormsCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = count.toString(), style = MaterialTheme.typography.headlineMedium)
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun TaskItem(
    task: Task,
    onTaskClick: (String) -> Unit
) {
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    SormsCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SormsBadge(
                    text = task.priority.name,
                    tone = when (task.priority) {
                        Priority.HIGH -> BadgeTone.Error
                        Priority.MEDIUM -> BadgeTone.Warning
                        Priority.LOW -> BadgeTone.Default
                    }
                )
                SormsBadge(
                    text = task.status.name.replace("_", " "),
                    tone = if (task.status == Status.COMPLETED) BadgeTone.Success else BadgeTone.Default
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = task.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            task.dueDate?.let {
                Text(text = "Hạn chót: ${dateFormatter.format(it)}", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(16.dp))
            SormsButton(
                onClick = { onTaskClick(task.id) },
                text = "Xem chi tiết",
                variant = ButtonVariant.Secondary
            )
        }
    }
}
