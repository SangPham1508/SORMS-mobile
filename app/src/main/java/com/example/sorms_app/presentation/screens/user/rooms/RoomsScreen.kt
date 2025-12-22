package com.example.sorms_app.presentation.screens.user.rooms

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sorms_app.data.models.RoomData
import com.example.sorms_app.presentation.viewmodel.RoomViewModel

@Composable
fun RoomsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onBook: (RoomData) -> Unit = {},
    viewModel: RoomViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5F8))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Danh sách phòng",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "↩",
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onBack() }
                        .padding(8.dp),
                    fontSize = 18.sp
                )
            }
        }

        Divider(color = Color(0xFFE5E7EB))

        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFFF7A1A))
                }
            }
            uiState.errorMessage != null -> {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "⚠", fontSize = 32.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(text = uiState.errorMessage ?: "Lỗi không xác định", color = Color(0xFFDC2626))
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { viewModel.refresh() }) { Text("Thử lại") }
                }
            }
            else -> {
                Column(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Filters: RoomType dropdown
                    var expanded = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
                    val roomTypes = uiState.roomTypes
                    val selectedType = uiState.selectedRoomTypeId
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Loại phòng:", style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.width(8.dp))
                            Box {
                                OutlinedButton(onClick = { expanded.value = true }) {
                                    val label = when (selectedType) {
                                        null -> "Tất cả"
                                        else -> roomTypes.firstOrNull { it.id == selectedType }?.name ?: "Tất cả"
                                    }
                                    Text(label)
                                }
                                DropdownMenu(
                                    expanded = expanded.value,
                                    onDismissRequest = { expanded.value = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Tất cả") },
                                        onClick = {
                                            expanded.value = false
                                            viewModel.selectRoomType(null)
                                        }
                                    )
                                    roomTypes.forEach { type ->
                                        DropdownMenuItem(
                                            text = { Text(type.name ?: type.code ?: "") },
                                            onClick = {
                                                expanded.value = false
                                                type.id?.let { viewModel.selectRoomType(it) }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    uiState.rooms.forEach { room ->
                        RoomItemCard(
                            room = room,
                            onClick = {
                                if (room.isAvailable) onBook(room)
                            }
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun RoomItemCard(
    room: RoomData,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = room.isAvailable) { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (room.isAvailable) Color.White else Color(0xFFF9FAFB)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (room.isAvailable) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)),
                contentAlignment = Alignment.Center
            ) { Text("🏠", fontSize = 24.sp) }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(text = room.number, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(text = room.type, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF6B7280))
                Spacer(Modifier.height(4.dp))
                Row {
                    Text(text = room.floor?.toString() ?: "", color = Color.Gray, fontSize = 12.sp)
                    Text(text = " • ", color = Color.Gray, fontSize = 12.sp)
                    Text(text = room.capacity ?: "", color = Color.Gray, fontSize = 12.sp)
                }
            }

            Spacer(Modifier.width(8.dp))

            AssistChip(
                onClick = { if (room.isAvailable) onClick() },
                label = { Text(if (room.isAvailable) "Còn trống" else "Đã đầy") },
            )
        }
    }
}


