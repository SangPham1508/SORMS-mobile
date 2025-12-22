package com.example.sorms_app.presentation.screens.user

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
import com.example.sorms_app.domain.model.Booking
import com.example.sorms_app.presentation.components.*
import com.example.sorms_app.presentation.viewmodel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    onBookingSelected: (Booking) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadHistory()
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { 
                Text(
                    text = "Lịch sử booking",
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
            
            uiState.bookings.isEmpty() -> {
                SormsEmptyState(
                    title = "Chưa có lịch sử",
                    subtitle = "Bạn chưa có booking nào. Hãy đặt phòng để bắt đầu!"
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
                            selectedFilter = uiState.selectedFilter,
                            onFilterChanged = viewModel::setFilter
                        )
                    }
                    
                    // Statistics Card
                    item {
                        StatisticsCard(
                            totalBookings = uiState.bookings.size,
                            completedBookings = uiState.bookings.count { it.status == "COMPLETED" },
                            cancelledBookings = uiState.bookings.count { it.status == "CANCELLED" }
                        )
                    }
                    
                    // Bookings List
                    items(uiState.filteredBookings) { booking ->
                        HistoryBookingCard(
                            booking = booking,
                            onBookingClick = { onBookingSelected(booking) }
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
    val filters = listOf("Tất cả", "Hoàn thành", "Đã hủy", "Đang diễn ra")
    
    SormsCard {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Lọc theo trạng thái",
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
private fun StatisticsCard(
    totalBookings: Int,
    completedBookings: Int,
    cancelledBookings: Int
) {
    SormsCard {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Thống kê",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatisticItem(
                    icon = Icons.Default.Hotel,
                    label = "Tổng booking",
                    value = totalBookings.toString(),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                
                StatisticItem(
                    icon = Icons.Default.CheckCircle,
                    label = "Hoàn thành",
                    value = completedBookings.toString(),
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )
                
                StatisticItem(
                    icon = Icons.Default.Cancel,
                    label = "Đã hủy",
                    value = cancelledBookings.toString(),
                    color = MaterialTheme.colorScheme.error,
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
            modifier = Modifier.size(32.dp),
            tint = color
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = value,
            fontSize = 20.sp,
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
private fun HistoryBookingCard(
    booking: Booking,
    onBookingClick: () -> Unit
) {
    SormsCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Booking Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = booking.roomName ?: "Phòng #${booking.roomId}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = "Booking #${booking.id}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                SormsBadge(
                    text = getBookingStatusText(booking.status),
                    tone = getBookingStatusBadgeTone(booking.status)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Booking Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BookingDetailItem(
                    icon = Icons.Default.DateRange,
                    label = "Check-in",
                    value = formatDate(booking.checkInDate?.toString()),
                    modifier = Modifier.weight(1f)
                )
                
                BookingDetailItem(
                    icon = Icons.Default.Schedule,
                    label = "Check-out", 
                    value = formatDate(booking.checkOutDate?.toString()),
                    modifier = Modifier.weight(1f)
                )
                
                BookingDetailItem(
                    icon = Icons.Default.People,
                    label = "Khách",
                    value = "${booking.numberOfGuests ?: 1} người",
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Special Requests
            booking.notes?.let { requests ->
                if (requests.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "Yêu cầu đặc biệt: $requests",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Action Button
            OutlinedButton(
                onClick = onBookingClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Xem chi tiết")
            }
        }
    }
}

@Composable
private fun BookingDetailItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
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
            tint = MaterialTheme.colorScheme.primary
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
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// Helper functions
private fun getBookingStatusText(status: String): String {
    return when (status.uppercase()) {
        "CONFIRMED" -> "Đã xác nhận"
        "CHECKED_IN" -> "Đã check-in"
        "CHECKED_OUT" -> "Đã check-out"
        "COMPLETED" -> "Hoàn thành"
        "CANCELLED" -> "Đã hủy"
        "PENDING" -> "Chờ xử lý"
        else -> status
    }
}

private fun getBookingStatusBadgeTone(status: String): BadgeTone {
    return when (status.uppercase()) {
        "COMPLETED" -> BadgeTone.Success
        "CONFIRMED", "CHECKED_IN" -> BadgeTone.Default
        "CANCELLED" -> BadgeTone.Error
        "PENDING" -> BadgeTone.Warning
        else -> BadgeTone.Default
    }
}

private fun formatDate(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return "N/A"
    return try {
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(dateString)
        val formatter = SimpleDateFormat("dd/MM", Locale.getDefault())
        formatter.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}