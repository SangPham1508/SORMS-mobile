package com.example.sorms_app.presentation.screens.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sorms_app.data.models.RoomData
import com.example.sorms_app.presentation.components.*
import com.example.sorms_app.presentation.theme.DesignSystem
import com.example.sorms_app.presentation.utils.StatusUtils
import com.example.sorms_app.presentation.viewmodel.RoomViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomsScreen(
    onNavigateBack: () -> Unit,
    onRoomSelected: (RoomData) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RoomViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Load data when screen is first composed
    LaunchedEffect(Unit) {
        viewModel.loadRooms()
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Top App Bar
        SormsTopAppBar(
            title = "Đặt phòng",
            onNavigateBack = onNavigateBack
        )

        // Content
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
                    // Filter Section
                    item {
                        FilterSection(
                            selectedFilter = uiState.selectedFilter,
                            onFilterChanged = viewModel::setFilter
                        )
                    }
                    
                    // Rooms List
                    items(uiState.filteredRooms) { room ->
                        RoomCard(
                            room = room,
                            onRoomClick = { onRoomSelected(room) }
                        )
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
        Column(
            modifier = Modifier.padding(DesignSystem.Spacing.cardContentPadding)
        ) {
            Text(
                text = "Lọc phòng",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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
                .padding(16.dp)
        ) {
            // Room Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = room.name ?: room.code ?: "Phòng #${room.id}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = room.code ?: "R-${room.id}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                SormsBadge(
                    text = StatusUtils.getRoomStatusText(room.status ?: "UNKNOWN"),
                    tone = StatusUtils.getRoomStatusBadgeTone(room.status ?: "UNKNOWN")
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Room Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
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
                    value = "Liên hệ", // No price in RoomData
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Description
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
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Action Button
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

