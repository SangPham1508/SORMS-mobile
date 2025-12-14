package com.example.sorms_app.presentation.screens.user.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DryCleaning
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sorms_app.domain.model.Booking
import com.example.sorms_app.domain.model.Notification
import com.example.sorms_app.presentation.components.SormsCard
import com.example.sorms_app.presentation.components.SormsEmptyState
import com.example.sorms_app.presentation.components.SormsLoading
import com.example.sorms_app.presentation.viewmodel.UserDashboardViewModel

@Composable
fun UserDashboardScreen(viewModel: UserDashboardViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> {
            SormsLoading()
        }
        uiState.errorMessage != null -> {
            SormsEmptyState(
                title = "Lỗi",
                subtitle = uiState.errorMessage ?: "Không thể tải dữ liệu dashboard."
            )
        }
        else -> {
            DashboardContent(
                currentBooking = uiState.currentBooking,
                notifications = uiState.notifications
            )
        }
    }
}

@Composable
private fun DashboardContent(currentBooking: Booking?, notifications: List<Notification>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            currentBooking?.let {
                CurrentRoomCard(booking = it)
            }
        }

        item {
            QuickActionsGrid()
        }

        item {
            Text(
                text = "Thông báo gần đây",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        if (notifications.isEmpty()) {
            item {
                SormsCard {
                    Text("Không có thông báo nào", modifier = Modifier.padding(16.dp))
                }
            }
        } else {
            items(notifications) { notification ->
                NotificationItem(notification = notification)
            }
        }
    }
}

@Composable
private fun CurrentRoomCard(booking: Booking) {
    SormsCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Phòng hiện tại", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${booking.roomName} - ${booking.buildingName}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun QuickActionsGrid() {
    Column {
        Text(
            text = "Truy cập nhanh",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            QuickActionItem(icon = Icons.Default.CleaningServices, label = "Dọn phòng", modifier = Modifier.weight(1f))
            QuickActionItem(icon = Icons.Default.DryCleaning, label = "Giặt ủi", modifier = Modifier.weight(1f))
            QuickActionItem(icon = Icons.Default.Report, label = "Báo cáo sự cố", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun QuickActionItem(icon: ImageVector, label: String, modifier: Modifier = Modifier) {
    SormsCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun NotificationItem(notification: Notification) {
    SormsCard {
        Text(
            text = notification.message,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp)
        )
    }
}
