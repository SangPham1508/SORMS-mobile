package com.example.sorms_app.presentation.screens.staff

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sorms_app.domain.model.Task
import com.example.sorms_app.presentation.components.*
import com.example.sorms_app.presentation.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffTasksScreen(
    onNavigateBack: () -> Unit,
    onTaskSelected: (Task) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedFilter by remember { mutableStateOf("Tất cả") }
    
    LaunchedEffect(Unit) {
        viewModel.loadTasks()
    }
    
    val filteredTasks = remember(uiState.tasks, selectedFilter) {
        when (selectedFilter) {
            "Chờ xử lý" -> uiState.tasks.filter { it.status.name == "PENDING" }
            "Đang thực hiện" -> uiState.tasks.filter { it.status.name == "IN_PROGRESS" }
            "Hoàn thành" -> uiState.tasks.filter { it.status.name == "COMPLETED" }
            "Quá hạn" -> uiState.tasks.filter { 
                it.status.name != "COMPLETED" && isOverdue(it.dueDate?.toString())
            }
            else -> uiState.tasks
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { 
                Text(
                    text = "Nhiệm vụ của tôi",
                    fontWeight = FontWeight.SemiBold
                ) 
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Quay lại"
                    )
                }
            },
            actions = {
                IconButton(onClick = { viewModel.refresh() }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Làm mới"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
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
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Filter Section
                    item {
                        FilterSection(
                            selectedFilter = selectedFilter,
                            onFilterChanged = { selectedFilter = it }
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
                            onUpdateStatus = { newStatus ->
                                viewModel.updateTaskStatus(task.id, newStatus)
                            }
                        )
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
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Lọc nhiệm vụ",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
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
            modifier = Modifier.padding(16.dp)
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
                    text = getTaskStatusText(task.status),
                    tone = getTaskStatusBadgeTone(task.status)
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TaskDetailItem(
                    icon = Icons.Default.DateRange,
                    label = "Hạn",
                    value = formatDate(task.dueDate?.toString()),
                    isOverdue = isOverdue(task.dueDate?.toString()) && task.status.name != "COMPLETED",
                    modifier = Modifier.weight(1f)
                )
                
                TaskDetailItem(
                    icon = Icons.Default.Flag,
                    label = "Ưu tiên",
                    value = getPriorityText(task.priority?.name),
                    modifier = Modifier.weight(1f)
                )
                
                TaskDetailItem(
                    icon = Icons.Default.Business,
                    label = "Phòng",
                    value = task.booking?.roomName ?: "N/A",
                    modifier = Modifier.weight(1f)
                )
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
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    isOverdue: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(20.dp),
            tint = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )
    }
}

// Helper functions
private fun getTaskStatusText(status: com.example.sorms_app.domain.model.Status): String {
    return when (status.name.uppercase()) {
        "PENDING" -> "Chờ xử lý"
        "IN_PROGRESS" -> "Đang thực hiện"
        "COMPLETED" -> "Hoàn thành"
        "CANCELLED" -> "Đã hủy"
        else -> status.name
    }
}

private fun getTaskStatusBadgeTone(status: com.example.sorms_app.domain.model.Status): BadgeTone {
    return when (status.name.uppercase()) {
        "COMPLETED" -> BadgeTone.Success
        "IN_PROGRESS" -> BadgeTone.Default
        "PENDING" -> BadgeTone.Warning
        "CANCELLED" -> BadgeTone.Error
        else -> BadgeTone.Default
    }
}

private fun getPriorityText(priority: String?): String {
    return when (priority?.uppercase()) {
        "HIGH" -> "Cao"
        "MEDIUM" -> "Trung bình"
        "LOW" -> "Thấp"
        else -> "Bình thường"
    }
}

private fun formatDate(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return "N/A"
    return try {
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(dateString)
        val formatter = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
        formatter.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}

private fun isOverdue(dateString: String?): Boolean {
    if (dateString.isNullOrEmpty()) return false
    return try {
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(dateString)
        date?.before(Date()) == true
    } catch (e: Exception) {
        false
    }
}