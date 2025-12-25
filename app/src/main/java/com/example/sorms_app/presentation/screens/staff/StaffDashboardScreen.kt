package com.example.sorms_app.presentation.screens.staff

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sorms_app.domain.model.Task
import com.example.sorms_app.domain.model.Priority
import com.example.sorms_app.domain.model.Status
import com.example.sorms_app.presentation.components.*
import com.example.sorms_app.presentation.theme.DesignSystem
import com.example.sorms_app.presentation.utils.DateUtils
import com.example.sorms_app.presentation.utils.PriorityUtils
import com.example.sorms_app.presentation.utils.StatusUtils
import com.example.sorms_app.presentation.viewmodel.TaskViewModel

data class StaffDashboardUiState(
    val isLoading: Boolean = false,
    val staffName: String = "Nhân viên",
    val totalTasks: Int = 0,
    val pendingTasks: Int = 0,
    val inProgressTasks: Int = 0,
    val completedTasks: Int = 0,
    val recentTasks: List<Task> = emptyList(),
    val error: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffDashboardScreen(
    onNavigateToTasks: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onTaskSelected: (Task) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = hiltViewModel()
) {
    // Use ViewModel state
    val taskUiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Mock UI state for now (will be replaced with real data from ViewModel)
    val uiState = remember {
        StaffDashboardUiState(
            staffName = "Nguyễn Văn B",
            totalTasks = taskUiState.tasks.size,
            pendingTasks = taskUiState.tasks.count { it.status.name == "PENDING" },
            inProgressTasks = taskUiState.tasks.count { it.status.name == "IN_PROGRESS" },
            completedTasks = taskUiState.tasks.count { it.status.name == "COMPLETED" },
            recentTasks = taskUiState.tasks.take(5)
        )
    }
    
    // Load data when screen is first composed
    LaunchedEffect(Unit) {
        viewModel.loadTasks()
    }

    Box(modifier = modifier.fillMaxSize()) {
    LazyColumn(
            modifier = Modifier
            .fillMaxSize()
            .padding(DesignSystem.Spacing.screenHorizontal),
        verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.md)
    ) {
        // Welcome Section
        item {
            StaffWelcomeSection(
                staffName = uiState.staffName,
                onNavigateToTasks = onNavigateToTasks,
                onNavigateToOrders = onNavigateToOrders
            )
        }

        // Task Summary Cards
        item {
            TaskSummarySection(
                totalTasks = uiState.totalTasks,
                pendingTasks = uiState.pendingTasks,
                inProgressTasks = uiState.inProgressTasks,
                completedTasks = uiState.completedTasks
            )
        }

        // Recent Tasks
        item {
            Text(
                text = "Công việc gần đây",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (uiState.isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else if (uiState.recentTasks.isEmpty()) {
            item {
                SormsEmptyState(
                    title = "Không có công việc",
                    subtitle = "Bạn chưa có công việc nào được phân công"
                )
            }
        } else {
            items(uiState.recentTasks.take(5)) { task ->
                TaskCard(
                    task = task,
                    onTaskClick = { onTaskSelected(task) },
                    onAcceptTask = { /* viewModel.acceptTask(task.id) */ },
                    onCompleteTask = { /* viewModel.completeTask(task.id) */ }
                )
            }
            
            // View All Tasks Button
            item {
                OutlinedButton(
                    onClick = onNavigateToTasks,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Xem tất cả công việc")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
                }
            }
        }
    }
}

@Composable
private fun StaffWelcomeSection(
    staffName: String,
    onNavigateToTasks: () -> Unit,
    onNavigateToOrders: () -> Unit
) {
    SormsCard {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Badge,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Nhân viên",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Chào $staffName!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Quản lý công việc và đơn hàng được phân công",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SormsButton(
                    onClick = onNavigateToTasks,
                    text = "Công việc",
                    variant = ButtonVariant.Primary,
                    modifier = Modifier.weight(1f)
                )
                
                SormsButton(
                    onClick = onNavigateToOrders,
                    text = "Đơn hàng",
                    variant = ButtonVariant.Secondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TaskSummarySection(
    totalTasks: Int,
    pendingTasks: Int,
    inProgressTasks: Int,
    completedTasks: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TaskSummaryCard(
            title = "Tổng cộng",
            count = totalTasks,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        
        TaskSummaryCard(
            title = "Chờ xử lý",
            count = pendingTasks,
            color = Color(0xFFF59E0B), // Amber
            modifier = Modifier.weight(1f)
        )
    }
    
    Spacer(modifier = Modifier.height(8.dp))
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TaskSummaryCard(
            title = "Đang làm",
            count = inProgressTasks,
            color = Color(0xFF3B82F6), // Blue
            modifier = Modifier.weight(1f)
        )
        
        TaskSummaryCard(
            title = "Hoàn thành",
            count = completedTasks,
            color = Color(0xFF10B981), // Green
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TaskSummaryCard(
    title: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            
            Text(
                text = title,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TaskCard(
    task: Task,
    onTaskClick: () -> Unit,
    onAcceptTask: () -> Unit,
    onCompleteTask: () -> Unit
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
                        text = "Mã: ${task.id}", // Use id instead of code
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    SormsBadge(
                        text = StatusUtils.getTaskStatusText(task.status.name),
                        tone = StatusUtils.getTaskStatusBadgeTone(task.status.name)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    SormsBadge(
                        text = PriorityUtils.getPriorityText(task.priority),
                        tone = PriorityUtils.getPriorityBadgeTone(task.priority)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Task Details
            task.description?.let { description ->
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2
                )
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Due Date
            task.dueDate?.let { dueDate ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = "Hạn: ${DateUtils.formatDate(dueDate)}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onTaskClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Chi tiết")
                }
                
                when (task.status) {
                    Status.PENDING -> {
                        SormsButton(
                            onClick = onAcceptTask,
                            text = "Nhận việc",
                            variant = ButtonVariant.Primary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Status.IN_PROGRESS -> {
                        SormsButton(
                            onClick = onCompleteTask,
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



