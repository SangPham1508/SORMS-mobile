package com.example.sorms_app.presentation.screens.staff.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sorms_app.domain.model.Status
import com.example.sorms_app.domain.model.Task
import com.example.sorms_app.presentation.components.*
import com.example.sorms_app.presentation.theme.DesignSystem
import com.example.sorms_app.presentation.utils.DateUtils
import com.example.sorms_app.presentation.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: String,
    onNavigateBack: () -> Unit,
    viewModel: TaskViewModel = hiltViewModel()
) {
    LaunchedEffect(taskId) {
        viewModel.loadTaskById(taskId)
    }

    val uiState by viewModel.detailUiState.collectAsState()

    Scaffold(
        topBar = {
            SormsTopAppBar(
                title = "Chi tiết công việc",
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = {
            uiState.task?.let { task ->
                TaskActions(task = task, viewModel = viewModel)
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(DesignSystem.Spacing.screenHorizontal)
        ) {
            when {
                uiState.isLoading -> {
                    SormsLoading(modifier = Modifier.align(Alignment.Center))
                }
                uiState.errorMessage != null -> {
                    SormsEmptyState(
                        title = "Lỗi",
                        subtitle = uiState.errorMessage ?: "Không thể tải chi tiết công việc."
                    )
                }
                uiState.task != null -> {
                    TaskDetails(task = uiState.task!!)
                }
                else -> {
                    SormsEmptyState(
                        title = "Không tìm thấy",
                        subtitle = "Không tìm thấy công việc với ID này."
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskDetails(task: Task) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(text = task.title, style = MaterialTheme.typography.headlineSmall)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            task.priority?.let { priority ->
                SormsBadge(
                    text = priority.name,
                    tone = when (priority) {
                        com.example.sorms_app.domain.model.Priority.HIGH -> BadgeTone.Error
                        com.example.sorms_app.domain.model.Priority.MEDIUM -> BadgeTone.Warning
                        else -> BadgeTone.Default
                    }
                )
            }
            SormsBadge(
                text = task.status.name.replace("_", " "),
                tone = if (task.status == Status.COMPLETED) BadgeTone.Success else BadgeTone.Default
            )
        }

        DetailItem(label = "Mô tả", value = task.description ?: "(Không có mô tả)")
        DetailItem(label = "Người giao", value = task.assignedBy ?: "(Không rõ)")
        task.dueDate?.let {
            DetailItem(label = "Hạn chót", value = DateUtils.formatDate(it))
        }
    }
}

@Composable
private fun TaskActions(task: Task, viewModel: TaskViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (task.status == Status.PENDING) {
            SormsButton(
                onClick = { viewModel.updateTaskStatus(task.id, "REJECTED") },
                text = "Từ chối",
                modifier = Modifier.weight(1f),
                variant = ButtonVariant.Danger
            )
            SormsButton(
                onClick = { viewModel.updateTaskStatus(task.id, "IN_PROGRESS") }, // Assuming Accept moves to IN_PROGRESS
                text = "Chấp nhận",
                modifier = Modifier.weight(1f)
            )
        } else if (task.status == Status.IN_PROGRESS) {
            SormsButton(
                onClick = { viewModel.updateTaskStatus(task.id, "COMPLETED") },
                text = "Hoàn thành",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}
