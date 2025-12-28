package com.example.sorms_app.presentation.screens.user.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import com.example.sorms_app.domain.model.Booking
import com.example.sorms_app.presentation.components.ButtonVariant
import com.example.sorms_app.presentation.components.SormsBadge
import com.example.sorms_app.presentation.components.SormsButton
import com.example.sorms_app.presentation.components.SormsCard
import com.example.sorms_app.presentation.components.SormsEmptyState
import com.example.sorms_app.presentation.components.SormsTopAppBar
import com.example.sorms_app.presentation.theme.DesignSystem
import com.example.sorms_app.presentation.theme.SORMS_appTheme
import com.example.sorms_app.presentation.utils.DateUtils
import com.example.sorms_app.presentation.utils.StatusUtils
import com.example.sorms_app.presentation.viewmodel.HistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    onBookingSelected: (Booking) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadHistory()
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            SormsTopAppBar(
                title = "Lịch sử booking",
                onNavigateBack = onNavigateBack
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
                    Box(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(DesignSystem.Spacing.screenHorizontal),
                            verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.listItemSpacing)
                        ) {
                            item {
                                FilterSection(
                                    selectedFilter = uiState.selectedFilter,
                                    onFilterChanged = viewModel::setFilter
                                )
                            }

                            item {
                                StatisticsCard(
                                    totalBookings = uiState.bookings.size,
                                    completedBookings = uiState.bookings.count { booking -> booking.status == "COMPLETED" },
                                    cancelledBookings = uiState.bookings.count { booking -> booking.status == "CANCELLED" }
                                )
                            }

                            items(items = uiState.filteredBookings) { booking ->
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
    }
}

@Composable
private fun FilterSection(
    selectedFilter: String,
    onFilterChanged: (String) -> Unit
) {
    val filters = listOf("Tất cả", "Hoàn thành", "Đã hủy", "Đang diễn ra")

    SormsCard {
        Column(modifier = Modifier.padding(DesignSystem.Spacing.cardContentPadding)) {  // Sử dụng DesignSystem spacing
            Text(
                text = "Lọc theo trạng thái",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(DesignSystem.Spacing.sm))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.elementSpacing)) {  // Tăng spacing
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
        Column(modifier = Modifier.padding(DesignSystem.Spacing.cardContentPadding)) {  // Sử dụng DesignSystem spacing
            Text(
                text = "Thống kê",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(DesignSystem.Spacing.md))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.md)  // Sử dụng DesignSystem spacing
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
        androidx.compose.material3.Icon(
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
                .padding(DesignSystem.Spacing.cardContentPadding)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
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
                    text = StatusUtils.getBookingStatusText(booking.status),
                    tone = StatusUtils.getBookingStatusBadgeTone(booking.status)
                )
            }

            Spacer(modifier = Modifier.height(DesignSystem.Spacing.sm))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.md)
            ) {
                BookingDetailItem(
                    icon = Icons.Default.DateRange,
                    label = "Check-in",
                    value = DateUtils.formatDate(booking.checkInDate?.toString()),
                    modifier = Modifier.weight(1f)
                )

                BookingDetailItem(
                    icon = Icons.Default.Schedule,
                    label = "Check-out",
                    value = DateUtils.formatDate(booking.checkOutDate?.toString()),
                    modifier = Modifier.weight(1f)
                )

                BookingDetailItem(
                    icon = Icons.Default.People,
                    label = "Khách",
                    value = "${booking.numberOfGuests ?: 1} người",
                    modifier = Modifier.weight(1f)
                )
            }

            booking.notes?.let { requests ->
                if (requests.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(DesignSystem.Spacing.sm))
                    Text(
                        text = "Yêu cầu đặc biệt: $requests",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(DesignSystem.Spacing.md))

            SormsButton(
                onClick = onBookingClick,
                text = "Xem chi tiết",
                variant = ButtonVariant.Secondary,
                isOutlined = true,
                modifier = Modifier.fillMaxWidth()
            )
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
        androidx.compose.material3.Icon(
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

@Preview(showBackground = true, name = "History Screen")
@Composable
private fun HistoryScreenPreview() {
    SORMS_appTheme {
        HistoryScreen(
            onNavigateBack = {},
            onBookingSelected = {}
        )
    }
}
