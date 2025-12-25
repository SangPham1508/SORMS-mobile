package com.example.sorms_app.presentation.screens.user.rooms

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sorms_app.data.models.RoomData
import com.example.sorms_app.presentation.components.SormsButton
import com.example.sorms_app.presentation.components.SormsCard
import com.example.sorms_app.presentation.components.SormsEmptyState
import com.example.sorms_app.presentation.components.SormsLoading
import com.example.sorms_app.presentation.components.SormsTopAppBar
import com.example.sorms_app.presentation.theme.DesignSystem
import com.example.sorms_app.presentation.utils.DateUtils
import com.example.sorms_app.presentation.viewmodel.RoomBookingViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomsBookingScreen(
    onBookingSuccess: () -> Unit,
    viewModel: RoomBookingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Form states
    var selectedRoom by remember { mutableStateOf<RoomData?>(null) }
    var checkInDate by remember { mutableStateOf<Date?>(null) }
    var checkOutDate by remember { mutableStateOf<Date?>(null) }
    var numGuests by remember { mutableStateOf(1) }
    var note by remember { mutableStateOf("") }
    var showBookingDialog by remember { mutableStateOf(false) }

    // Handle booking success
    LaunchedEffect(uiState.bookingSuccess) {
        if (uiState.bookingSuccess) {
            Toast.makeText(context, "Đặt phòng thành công! Đang chờ phê duyệt.", Toast.LENGTH_LONG).show()
            viewModel.resetBookingState()
            onBookingSuccess()
        }
    }

    // Handle booking error
    LaunchedEffect(uiState.bookingError) {
        uiState.bookingError?.let { error ->
            Toast.makeText(context, "Lỗi: $error", Toast.LENGTH_LONG).show()
            viewModel.resetBookingState()
        }
    }

    // Date pickers
    val checkInPicker = remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val cal = Calendar.getInstance()
                cal.set(year, month, day)
                checkInDate = cal.time
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis() - 1000
        }
    }

    val checkOutPicker = remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val cal = Calendar.getInstance()
                cal.set(year, month, day)
                checkOutDate = cal.time
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis() - 1000
        }
    }

    Scaffold(
        topBar = {
            SormsTopAppBar(
                title = "Đặt phòng",
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Làm mới")
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                SormsLoading(modifier = Modifier.padding(innerPadding))
            }
            uiState.errorMessage != null -> {
                SormsEmptyState(
                    title = "Lỗi",
                    subtitle = uiState.errorMessage ?: "Không thể tải danh sách phòng.",
                    modifier = Modifier.padding(innerPadding)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(DesignSystem.Spacing.screenHorizontal),
                    verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.listItemSpacing)
                ) {
                    // Room Type Filter
                    item {
                        RoomTypeFilter(
                            roomTypes = uiState.roomTypes,
                            selectedTypeId = uiState.selectedRoomTypeId,
                            onTypeSelected = { viewModel.selectRoomType(it) }
                        )
                    }

                    // Available Rooms
                    item {
                        Text(
                            text = "Phòng trống (${uiState.rooms.count { it.isAvailable }})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = DesignSystem.Spacing.sm)
                        )
                    }

                    if (uiState.rooms.isEmpty()) {
                        item {
                            SormsCard {
                                Text(
                                    text = "Không có phòng trống",
                                    modifier = Modifier.padding(DesignSystem.Spacing.lg),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    } else {
                        items(uiState.rooms.filter { it.isAvailable }) { room ->
                            RoomBookingCard(
                                room = room,
                                isSelected = selectedRoom?.id == room.id,
                                onSelect = {
                                    selectedRoom = room
                                    showBookingDialog = true
                                }
                            )
                        }
                    }

                    // Unavailable rooms section
                    val unavailableRooms = uiState.rooms.filter { !it.isAvailable }
                    if (unavailableRooms.isNotEmpty()) {
                        item {
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = "Phòng đã có người (${unavailableRooms.size})",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = DesignSystem.Spacing.sm)
                            )
                        }

                        items(unavailableRooms) { room ->
                            RoomBookingCard(
                                room = room,
                                isSelected = false,
                                onSelect = { /* Cannot select unavailable room */ },
                                enabled = false
                            )
                        }
                    }

                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }

        // Booking Dialog
        if (showBookingDialog && selectedRoom != null) {
            AlertDialog(
                onDismissRequest = { 
                    showBookingDialog = false 
                    selectedRoom = null
                },
                title = { 
                    Text("Đặt phòng ${selectedRoom!!.number}")
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Room info
                        SormsCard {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(DesignSystem.Spacing.listItemSpacing),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.MeetingRoom,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = selectedRoom!!.type,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = "Tầng ${selectedRoom!!.floor} • ${selectedRoom!!.capacity}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Check-in date
                        OutlinedTextField(
                            value = checkInDate?.let { DateUtils.formatDate(it) } ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Ngày check-in *") },
                            trailingIcon = {
                                IconButton(onClick = { checkInPicker.show() }) {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = null)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { checkInPicker.show() }
                        )

                        // Check-out date
                        OutlinedTextField(
                            value = checkOutDate?.let { DateUtils.formatDate(it) } ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Ngày check-out *") },
                            trailingIcon = {
                                IconButton(onClick = { checkOutPicker.show() }) {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = null)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { checkOutPicker.show() }
                        )

                        // Number of guests
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Số khách:", style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.weight(1f))
                            IconButton(
                                onClick = { if (numGuests > 1) numGuests-- },
                                enabled = numGuests > 1
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Giảm")
                            }
                            Text(
                                text = numGuests.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { numGuests++ }) {
                                Icon(Icons.Default.Add, contentDescription = "Tăng")
                            }
                        }

                        // Note
                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            label = { Text("Ghi chú (tùy chọn)") },
                            placeholder = { Text("Ví dụ: Cần phòng yên tĩnh...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            maxLines = 3
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (checkInDate == null || checkOutDate == null) {
                                Toast.makeText(context, "Vui lòng chọn ngày check-in và check-out", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (checkOutDate!! <= checkInDate!!) {
                                Toast.makeText(context, "Ngày check-out phải sau ngày check-in", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            viewModel.createBooking(
                                roomId = selectedRoom!!.id,
                                checkInDate = checkInDate!!,
                                checkOutDate = checkOutDate!!,
                                numGuests = numGuests,
                                note = note.takeIf { it.isNotBlank() }
                            )
                            showBookingDialog = false
                        },
                        enabled = !uiState.isBooking
                    ) {
                        if (uiState.isBooking) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(Modifier.width(8.dp))
                        }
                        Text("Đặt phòng")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        showBookingDialog = false
                        selectedRoom = null
                    }) {
                        Text("Hủy")
                    }
                }
            )
        }
    }
}

@Composable
private fun RoomTypeFilter(
    roomTypes: List<com.example.sorms_app.data.models.RoomTypeResponse>,
    selectedTypeId: Long?,
    onTypeSelected: (Long?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    SormsCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignSystem.Spacing.listItemSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.FilterList,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(8.dp))
            Text(text = "Loại phòng:", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.width(8.dp))
            Box {
                OutlinedButton(onClick = { expanded = true }) {
                    val label = when (selectedTypeId) {
                        null -> "Tất cả"
                        else -> roomTypes.firstOrNull { it.id == selectedTypeId }?.name ?: "Tất cả"
                    }
                    Text(label)
                    Icon(
                        if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Tất cả") },
                        onClick = {
                            expanded = false
                            onTypeSelected(null)
                        },
                        leadingIcon = {
                            if (selectedTypeId == null) {
                                Icon(Icons.Default.Check, contentDescription = null)
                            }
                        }
                    )
                    roomTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.name ?: type.code ?: "") },
                            onClick = {
                                expanded = false
                                type.id?.let { onTypeSelected(it) }
                            },
                            leadingIcon = {
                                if (selectedTypeId == type.id) {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoomBookingCard(
    room: RoomData,
    isSelected: Boolean,
    onSelect: () -> Unit,
    enabled: Boolean = true
) {
    val containerColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        !enabled -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled) { onSelect() },
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (enabled) 2.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(DesignSystem.Spacing.cardContentPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Room icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (room.isAvailable) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.MeetingRoom,
                    contentDescription = null,
                    tint = if (room.isAvailable) Color(0xFF16A34A) else Color(0xFFDC2626),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            // Room info
            Column(Modifier.weight(1f)) {
                Text(
                    text = room.number,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = room.type,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Row {
                    Text(
                        text = "Tầng ${room.floor ?: "?"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = " • ",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = room.capacity ?: "N/A",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            // Status chip
            AssistChip(
                onClick = { if (enabled) onSelect() },
                label = {
                    Text(
                        text = if (room.isAvailable) "Chọn" else "Đã đầy",
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                leadingIcon = if (room.isAvailable) {
                    { Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp)) }
                } else null,
                enabled = enabled
            )
        }
    }
}

