package com.example.sorms_app.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sorms_app.domain.model.Booking
import com.example.sorms_app.presentation.components.*
import com.example.sorms_app.presentation.viewmodel.UserDashboardViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboardScreen(
    onNavigateToRooms: () -> Unit,
    onNavigateToServices: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToFaceRegister: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UserDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Section
        item {
            WelcomeSection(
                userName = uiState.userName,
                onBookRoom = onNavigateToRooms,
                onOrderService = onNavigateToServices
            )
        }

        // Current Booking Section
        item {
            CurrentBookingSection(
                currentBooking = uiState.currentBooking,
                onBookRoom = onNavigateToRooms,
                onOrderService = onNavigateToServices,
                onViewOrders = onNavigateToOrders,
                onFaceRegister = onNavigateToFaceRegister
            )
        }

        // Quick Actions
        item {
            QuickActionsSection(
                onNavigateToRooms = onNavigateToRooms,
                onNavigateToServices = onNavigateToServices,
                onNavigateToOrders = onNavigateToOrders,
                onNavigateToHistory = onNavigateToHistory
            )
        }

        // Summary Cards
        item {
            SummarySection(
                activeBookings = uiState.activeBookingsCount,
                serviceOrders = uiState.serviceOrdersCount,
                unpaidOrders = uiState.unpaidOrdersCount
            )
        }
    }
}

@Composable
private fun WelcomeSection(
    userName: String,
    onBookRoom: () -> Unit,
    onOrderService: () -> Unit
) {
    SormsCard {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Chào mừng trở lại,",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Text(
                text = userName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            Text(
                text = "Quản lý nhanh đặt phòng, dịch vụ và hóa đơn của bạn.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SormsButton(
                    onClick = onBookRoom,
                    text = "Đặt phòng",
                    variant = ButtonVariant.Primary,
                    modifier = Modifier.weight(1f)
                )
                
                SormsButton(
                    onClick = onOrderService,
                    text = "Đặt dịch vụ",
                    variant = ButtonVariant.Secondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun CurrentBookingSection(
    currentBooking: Booking?,
    onBookRoom: () -> Unit,
    onOrderService: () -> Unit,
    onViewOrders: () -> Unit,
    onFaceRegister: () -> Unit
) {
    SormsCard {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (currentBooking != null) "Phòng hiện tại" else "Chưa có phòng đặt",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = if (currentBooking != null) 
                            "Thông tin đặt phòng đang diễn ra" 
                        else 
                            "Bạn chưa có phòng nào đang được đặt",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                currentBooking?.let { booking ->
                    SormsBadge(
                        text = getStatusText(booking.status),
                        tone = getStatusBadgeTone(booking.status)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (currentBooking != null) {
                // Booking Details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BookingInfoCard(
                        title = "Phòng",
                        value = currentBooking.roomName,
                        modifier = Modifier.weight(1f)
                    )
                    
                    BookingInfoCard(
                        title = "Check-in",
                        value = formatDate(currentBooking.checkInDate),
                        modifier = Modifier.weight(1f)
                    )
                    
                    BookingInfoCard(
                        title = "Check-out",
                        value = formatDate(currentBooking.checkOutDate),
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Action Buttons
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        OutlinedButton(
                            onClick = onBookRoom,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Đặt phòng mới")
                        }
                    }
                    
                    item {
                        OutlinedButton(
                            onClick = onOrderService,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Đặt dịch vụ")
                        }
                    }
                    
                    item {
                        OutlinedButton(
                            onClick = onViewOrders,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Xem hóa đơn")
                        }
                    }
                    
                    item {
                        OutlinedButton(
                            onClick = onFaceRegister,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Đăng ký khuôn mặt")
                        }
                    }
                }
            } else {
                // No booking state
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Bạn chưa có phòng nào đang được đặt",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    SormsButton(
                        onClick = onBookRoom,
                        text = "Đặt phòng ngay",
                        variant = ButtonVariant.Primary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun BookingInfoCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun QuickActionsSection(
    onNavigateToRooms: () -> Unit,
    onNavigateToServices: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val actions = listOf(
        QuickAction("Đặt phòng", Icons.Default.Home, onNavigateToRooms),
        QuickAction("Dịch vụ", Icons.Default.CleaningServices, onNavigateToServices),
        QuickAction("Hóa đơn", Icons.Default.Receipt, onNavigateToOrders),
        QuickAction("Lịch sử", Icons.Default.History, onNavigateToHistory)
    )
    
    SormsCard {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Thao tác nhanh",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                actions.forEach { action ->
                    QuickActionItem(
                        action = action,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionItem(
    action: QuickAction,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = action.onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.title,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = action.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SummarySection(
    activeBookings: Int,
    serviceOrders: Int,
    unpaidOrders: Int
) {
    SormsCard {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tóm tắt",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = Color(0xFF10B981), // Green
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryItem("Đặt phòng đang ở", activeBookings.toString())
                SummaryItem("Dịch vụ đã đặt", serviceOrders.toString())
                SummaryItem("Hóa đơn chờ", unpaidOrders.toString())
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = { /* Navigate to history */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Xem lịch sử thuê")
            }
        }
    }
}

@Composable
private fun SummaryItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// Helper data classes and functions
private data class QuickAction(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

private fun getStatusText(status: String): String {
    return when (status.uppercase()) {
        "PENDING" -> "Chờ duyệt"
        "APPROVED" -> "Đã duyệt"
        "CHECKED_IN" -> "Đã check-in"
        "CHECKED_OUT" -> "Đã check-out"
        "CANCELLED" -> "Đã hủy"
        "REJECTED" -> "Từ chối"
        else -> status
    }
}

private fun getStatusBadgeTone(status: String): BadgeTone {
    return when (status.uppercase()) {
        "CHECKED_IN", "APPROVED" -> BadgeTone.Success
        "PENDING" -> BadgeTone.Warning
        "CANCELLED", "REJECTED" -> BadgeTone.Error
        else -> BadgeTone.Default
    }
}

private fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("dd/MM", Locale.getDefault())
    return formatter.format(date)
}