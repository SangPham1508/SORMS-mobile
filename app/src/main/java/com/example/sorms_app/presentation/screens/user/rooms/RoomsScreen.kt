package com.example.sorms_app.presentation.screens.user.rooms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sorms_app.data.models.RoomData
import com.example.sorms_app.presentation.components.*
import com.example.sorms_app.presentation.theme.DesignSystem
import com.example.sorms_app.presentation.theme.SORMS_appTheme
import com.example.sorms_app.presentation.utils.StatusUtils
import com.example.sorms_app.presentation.viewmodel.RoomViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToFaceRegister: () -> Unit,
    onBookingSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RoomViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadRooms()
    }

    // Show booking modal
    MultiStepBookingModal(
        room = uiState.selectedRoomForBooking,
        isOpen = uiState.isBookingModalOpen,
        onDismiss = viewModel::closeBookingModal,
        onBookingSuccess = {
            viewModel.closeBookingModal()
            onBookingSuccess()
        },
        onFaceRegisterClick = onNavigateToFaceRegister,
        checkFaceStatus = {
            coroutineScope.launch {
                viewModel.checkFaceStatus()
            }.join()
            // This is a simplified way; ideally, the result should be passed back.
            // For now, assume the ViewModel handles the state.
            viewModel.checkFaceStatus() // Return the result
        },
        createBooking = { roomId, checkInDate, checkInTime, checkOutDate, checkOutTime, numGuests, note ->
            coroutineScope.launch {
                viewModel.createBooking(
                    roomId,
                    checkInDate,
                    checkInTime,
                    checkOutDate,
                    checkOutTime,
                    numGuests,
                    note
                )
            }.join()
            viewModel.createBooking(roomId, checkInDate, checkInTime, checkOutDate, checkOutTime, numGuests, note)
        }
    )

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            SormsTopAppBar(
                title = "Đặt phòng",
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

                !uiState.errorMessage.isNullOrBlank() -> {
                    SormsEmptyState(
                        title = "Có lỗi xảy ra",
                        subtitle = uiState.errorMessage ?: "Lỗi không xác định"
                    )
                }

                uiState.rooms.isEmpty() -> {
                    SormsEmptyState(
                        title = "Không có phòng",
                        subtitle = "Hiện tại không có phòng nào khả dụng"
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

                            items(items = uiState.filteredRooms) { room ->
                                RoomCard(
                                    room = room,
                                    onRoomClick = { viewModel.openBookingModal(room) }
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
    val filters = listOf("Tất cả", "Khả dụng", "Đang bảo trì")

    SormsCard {
        Column(modifier = Modifier.padding(DesignSystem.Spacing.cardContentPadding)) {
            Text(
                text = "Lọc phòng",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(DesignSystem.Spacing.sm))

            Row(horizontalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.elementSpacing)) {  // Tăng spacing
                filters.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { onFilterChanged(filter) },
                        label = { Text(filter) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun RoomCard(
    room: RoomData,
    onRoomClick: () -> Unit
) {
    SormsCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignSystem.Spacing.cardContentPadding)  // Sử dụng DesignSystem spacing
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = room.name ?: room.code ?: "Phòng #${room.id}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(DesignSystem.Spacing.xs))
                    
                    Text(
                        text = room.code ?: "R-${room.id}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)  // Tăng contrast
                    )
                }

                SormsBadge(
                    text = StatusUtils.getRoomStatusText(room.status ?: "UNKNOWN"),
                    tone = StatusUtils.getRoomStatusBadgeTone(room.status ?: "UNKNOWN")
                )
            }

            Spacer(modifier = Modifier.height(DesignSystem.Spacing.sm))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.md)  // Sử dụng DesignSystem spacing
            ) {
                RoomDetailItem(
                    icon = Icons.Default.Business,
                    label = "Tầng",
                    value = room.floor?.toString() ?: "N/A",
                    modifier = Modifier.weight(1f)
                )

                RoomDetailItem(
                    icon = Icons.Default.People,
                    label = "Sức chứa",
                    value = room.capacity ?: "N/A",
                    modifier = Modifier.weight(1f)
                )

                RoomDetailItem(
                    icon = Icons.Default.AttachMoney,
                    label = "Giá",
                    value = "Liên hệ",
                    modifier = Modifier.weight(1f)
                )
            }

            room.description?.let { description ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(DesignSystem.Spacing.md))  // Sử dụng DesignSystem spacing

            SormsButton(
                onClick = onRoomClick,
                text = if (room.status == "AVAILABLE") "Đặt phòng" else "Xem chi tiết",
                variant = if (room.status == "AVAILABLE") ButtonVariant.Primary else ButtonVariant.Secondary,
                enabled = room.status == "AVAILABLE",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun RoomDetailItem(
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

@Preview(showBackground = true, name = "Rooms Screen")
@Composable
private fun RoomsScreenPreview() {
    SORMS_appTheme {
        RoomsScreen(
            onNavigateBack = {},
            onNavigateToFaceRegister = {},
            onBookingSuccess = {}
        )
    }
}
