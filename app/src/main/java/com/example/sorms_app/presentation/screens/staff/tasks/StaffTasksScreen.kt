package com.example.sorms_app.presentation.screens.staff.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sorms_app.domain.model.Priority
import com.example.sorms_app.domain.model.Task
import com.example.sorms_app.presentation.components.*
import com.example.sorms_app.presentation.theme.DesignSystem
import com.example.sorms_app.presentation.theme.SORMS_appTheme
import com.example.sorms_app.presentation.utils.DateUtils
import com.example.sorms_app.presentation.utils.PriorityUtils
import com.example.sorms_app.presentation.utils.StatusUtils
import com.example.sorms_app.presentation.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffTasksScreen(
    onNavigateBack: () -> Unit,
    onTaskSelected: (Task) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedFilter by remember { mutableStateOf("Tất cả") }

    // Load data when screen is first composed
    LaunchedEffect(Unit) {
        viewModel.loadTasks()
    }

    val filteredTasks = remember(uiState.tasks, selectedFilter) {
        when (selectedFilter) {
            "Chờ xử lý" -> uiState.tasks.filter { it.status.name == "PENDING" }
            "Đang thực hiện" -> uiState.tasks.filter { it.status.name == "IN_PROGRESS" }
            "Hoàn thành" -> uiState.tasks.filter { it.status.name == "COMPLETED" }
            "Quá hạn" -> uiState.tasks.filter {
                it.status.name != "COMPLETED" && DateUtils.isOverdue(it.dueDate?.toString())
            }

            else -> uiState.tasks
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar
            SormsTopAppBar(
                title = "Nhiệm vụ của tôi",
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.refresh()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Làm mới"
                        )
                    }
                }
            )

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.errorMessage != null -> {
                    SormsEmptyState(
                        title = "Có lỗi xảy ra",
                        subtitle = uiState.errorMessage!!
                    )
                }

                uiState.tasks.isEmpty() -> {
                    SormsEmptyState(
                        title = "Không có nhiệm vụ",
                        subtitle = "Bạn chưa được phân công nhiệm vụ nào"
                    )
                }

                else -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(DesignSystem.Spacing.screenHorizontal),
                            verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.listItemSpacing)
                        ) {
                            // Filter Section
                            item {
                                FilterSection(
                                    selectedFilter = selectedFilter,
                                    onFilterChanged = { filter -> selectedFilter = filter }
                                )
                            }

                            // Task Statistics
                            item {
                                TaskStatisticsCard(
                                    totalTasks = uiState.tasks.size,
                                    pendingTasks = uiState.tasks.count { it.status.name == "PENDING" },
                                    inProgressTasks = uiState.tasks.count { it.status.name == "IN_PROGRESS" },
                                    completedTasks = uiState.tasks.count { it.status.name == "COMPLETED" }
                                )
                            }

                            // Tasks List
                            items(filteredTasks) { task ->
                                TaskCard(
                                    task = task,
                                    onTaskClick = { onTaskSelected(task) },
                                    onUpdateStatus = { newStatus: String ->
                                        viewModel.updateTaskStatus(task.id, newStatus)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterSection(
            selectedFilter: String,
            onFilterChanged: (String) -> Unit
        ) {
    val filters = listOf("Tất cả", "Chờ xử lý", "Đang thực hiện", "Hoàn thành", "Quá hạn")

    SormsCard {
        Column(
            modifier = Modifier.padding(DesignSystem.Spacing.cardContentPadding)
        ) {
            Text(
                text = "Lọc nhiệm vụ",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(DesignSystem.Spacing.sm))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.elementSpacing)  // Tăng spacing
            ) {
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { onFilterChanged(filter) },
                        label = { Text(filter) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskStatisticsCard(
            totalTasks: Int,
            pendingTasks: Int,
            inProgressTasks: Int,
            completedTasks: Int
) {
    SormsCard {
        Column(
            modifier = Modifier.padding(DesignSystem.Spacing.cardContentPadding)
        ) {
            Text(
                text = "Thống kê nhiệm vụ",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatisticItem(
                    icon = Icons.Default.Assignment,
                    label = "Tổng",
                    value = totalTasks.toString(),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                StatisticItem(
                    icon = Icons.Default.Schedule,
                    label = "Chờ",
                    value = pendingTasks.toString(),
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )

                StatisticItem(
                    icon = Icons.Default.PlayArrow,
                    label = "Đang làm",
                    value = inProgressTasks.toString(),
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )

                StatisticItem(
                    icon = Icons.Default.CheckCircle,
                    label = "Xong",
                    value = completedTasks.toString(),
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatisticItem(
            icon: androidx.compose.ui.graphics.vector.ImageVector,
            label: String,
            value: String,
            color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = color
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )

        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun TaskCard(
            task: Task,
            onTaskClick: () -> Unit,
    onUpdateStatus: (String) -> Unit
) {
    SormsCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Task Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = task.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "Nhiệm vụ #${task.id}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                SormsBadge(
                    text = StatusUtils.getTaskStatusText(task.status.name),
                    tone = StatusUtils.getTaskStatusBadgeTone(task.status.name)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Task Description
            task.description?.let { description ->
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Task Details
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                task.dueDate?.let {
                    TaskDetailItem(
                        label = "Hạn chót",
                        value = DateUtils.formatDateTimeShort(it.toString()),
                        isOverdue = DateUtils.isOverdue(it.toString()) && task.status.name != "COMPLETED"
                    )
                }

                task.priority?.let {
                    TaskDetailItem(
                        label = "Ưu tiên",
                        value = PriorityUtils.getPriorityText(it.name)
                    )
                }

                task.booking?.roomName?.let {
                    TaskDetailItem(
                        label = "Phòng",
                        value = it
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onTaskClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Chi tiết")
                }

                when (task.status.name) {
                    "PENDING" -> {
                        SormsButton(
                            onClick = { onUpdateStatus("IN_PROGRESS") },
                            text = "Bắt đầu",
                            variant = ButtonVariant.Primary,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    "IN_PROGRESS" -> {
                        SormsButton(
                            onClick = { onUpdateStatus("COMPLETED") },
                            text = "Hoàn thành",
                            variant = ButtonVariant.Primary,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    else -> {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskDetailItem(
            label: String,
            value: String,
    isOverdue: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true, name = "Staff Tasks Screen")
@Composable
private fun StaffTasksScreenPreview() {
    SORMS_appTheme {
        StaffTasksScreen(
            onNavigateBack = {},
            onTaskSelected = {}
        )
    }
}