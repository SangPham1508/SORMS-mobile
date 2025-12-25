package com.example.sorms_app.presentation.screens.user.bookings

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sorms_app.domain.model.Booking
import com.example.sorms_app.presentation.utils.DateUtils
import java.util.Calendar
import java.util.Date
import com.example.sorms_app.presentation.components.BadgeTone
import com.example.sorms_app.presentation.components.SormsBadge
import com.example.sorms_app.presentation.components.SormsCard
import com.example.sorms_app.presentation.components.SormsEmptyState
import com.example.sorms_app.presentation.components.SormsLoading
import com.example.sorms_app.presentation.components.SormsTopAppBar
import com.example.sorms_app.presentation.theme.DesignSystem
import com.example.sorms_app.presentation.utils.StatusUtils
import com.example.sorms_app.presentation.viewmodel.BookingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingsScreen(
    viewModel: BookingViewModel = hiltViewModel(),
    onNavigateToOrders: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedBooking by remember { mutableStateOf<Booking?>(null) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    // Handle cancel success/error
    LaunchedEffect(uiState.cancelSuccess) {
        if (uiState.cancelSuccess) {
            Toast.makeText(context, "Đã hủy đặt phòng thành công", Toast.LENGTH_SHORT).show()
            viewModel.clearCancelState()
        }
    }

    LaunchedEffect(uiState.cancelError) {
        uiState.cancelError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearCancelState()
        }
    }

    Scaffold(
        topBar = {
            SormsTopAppBar(
                title = "Lịch sử đặt phòng",
                actions = {
                    IconButton(onClick = { viewModel.loadAllBookings() }) {
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
                    subtitle = uiState.errorMessage ?: "Không thể tải lịch sử đặt phòng.",
                    modifier = Modifier.padding(innerPadding)
                )
            }
            uiState.bookings.isEmpty() -> {
                SormsEmptyState(
                    title = "Chưa có đặt phòng",
                    subtitle = "Lịch sử đặt phòng của bạn sẽ được hiển thị ở đây.\nĐặt phòng để bắt đầu!",
                    modifier = Modifier.padding(innerPadding)
                )
            }
            else -> {
                BookingList(
                    bookings = uiState.bookings,
                    onEditClick = { booking ->
                        selectedBooking = booking
                        showEditDialog = true
                    },
                    onCancelClick = { booking ->
                        selectedBooking = booking
                        showCancelDialog = true
                    },
                    onViewOrderClick = { booking ->
                        onNavigateToOrders(booking.id)
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }

        // Edit booking dialog
        if (showEditDialog && selectedBooking != null) {
            EditBookingDialog(
                booking = selectedBooking!!,
                onDismiss = {
                    showEditDialog = false
                    selectedBooking = null
                },
                onConfirm = { checkIn, checkOut, numGuests, note ->
                    checkIn?.let { checkInDate ->
                        checkOut?.let { checkOutDate ->
                    viewModel.updateBooking(
                        bookingId = selectedBooking!!.id,
                                checkInDate = checkInDate,
                                checkOutDate = checkOutDate,
                        numGuests = numGuests,
                        note = note
                    )
                        }
                    }
                    showEditDialog = false
                    selectedBooking = null
                }
            )
        }

        // Cancel confirmation dialog
        if (showCancelDialog && selectedBooking != null) {
            AlertDialog(
                onDismissRequest = {
                    showCancelDialog = false
                    selectedBooking = null
                },
                icon = { Icon(Icons.Default.Warning, contentDescription = null) },
                title = { Text("Xác nhận hủy đặt phòng") },
                text = { 
                    Text("Bạn có chắc chắn muốn hủy đặt phòng ${selectedBooking?.code}?\n\nHành động này không thể hoàn tác.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.cancelBooking(selectedBooking!!.id)
                            showCancelDialog = false
                            selectedBooking = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Hủy đặt phòng")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showCancelDialog = false
                        selectedBooking = null
                    }) {
                        Text("Đóng")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditBookingDialog(
    booking: Booking,
    onDismiss: () -> Unit,
                onConfirm: (Date?, Date?, Int, String?) -> Unit
) {
    val context = LocalContext.current
    
    var checkInDate by remember { mutableStateOf<Date?>(booking.checkInDate) }
    var checkOutDate by remember { mutableStateOf<Date?>(booking.checkOutDate) }
    var numGuests by remember { mutableStateOf(booking.numGuests) }
    var note by remember { mutableStateOf(booking.note ?: "") }

    val checkInCalendar = Calendar.getInstance()
    booking.checkInDate?.let { checkInCalendar.time = it }

    val checkInPicker = remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val cal = Calendar.getInstance()
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, day)
                checkInDate = cal.time
            },
            checkInCalendar.get(Calendar.YEAR),
            checkInCalendar.get(Calendar.MONTH),
            checkInCalendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis() - 1000
        }
    }

    val checkOutCalendar = Calendar.getInstance()
    booking.checkOutDate?.let { checkOutCalendar.time = it }

    val checkOutPicker = remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val cal = Calendar.getInstance()
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, day)
                checkOutDate = cal.time
            },
            checkOutCalendar.get(Calendar.YEAR),
            checkOutCalendar.get(Calendar.MONTH),
            checkOutCalendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis() - 1000
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chỉnh sửa đặt phòng") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Mã: ${booking.code}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "Phòng: ${booking.roomName}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Divider()

                // Check-in date
                OutlinedTextField(
                    value = checkInDate?.let { DateUtils.formatDate(it) } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Ngày check-in") },
                    trailingIcon = {
                        IconButton(onClick = { checkInPicker.show() }) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Check-out date
                OutlinedTextField(
                    value = checkOutDate?.let { DateUtils.formatDate(it) } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Ngày check-out") },
                    trailingIcon = {
                        IconButton(onClick = { checkOutPicker.show() }) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Number of guests
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Số khách:")
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
                    label = { Text("Ghi chú") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val checkIn = checkInDate
                    val checkOut = checkOutDate
                    if (checkOut != null && checkIn != null && checkOut <= checkIn) {
                        Toast.makeText(context, "Ngày check-out phải sau check-in", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    onConfirm(checkIn, checkOut, numGuests, note.takeIf { it.isNotBlank() })
                }
            ) {
                Text("Lưu thay đổi")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

@Composable
private fun BookingList(
    bookings: List<Booking>,
    onEditClick: (Booking) -> Unit,
    onCancelClick: (Booking) -> Unit,
    onViewOrderClick: (Booking) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(DesignSystem.Spacing.screenHorizontal),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(bookings) { booking ->
            BookingCard(
                booking = booking,
                onEditClick = { onEditClick(booking) },
                onCancelClick = { onCancelClick(booking) },
                onViewOrderClick = { onViewOrderClick(booking) }
            )
        }
    }
}

@Composable
private fun BookingCard(
    booking: Booking,
    onEditClick: () -> Unit,
    onCancelClick: () -> Unit,
    onViewOrderClick: () -> Unit
) {
    val statusUpper = booking.status.uppercase()
    val canEdit = statusUpper == "PENDING"
    val canCancel = statusUpper == "PENDING"
    val hasOrder = statusUpper in listOf("APPROVED", "CHECKED_IN", "CHECKED_OUT")

    SormsCard {
        Column(modifier = Modifier.padding(DesignSystem.Spacing.cardContentPadding)) {
            // Header: Code + Status
            Row(
                horizontalArrangement = Arrangement.SpaceBetween, 
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.MeetingRoom,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = booking.code,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                SormsBadge(
                    text = StatusUtils.getBookingStatusText(booking.status),
                    tone = StatusUtils.getBookingStatusBadgeTone(booking.status)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Divider()
            Spacer(modifier = Modifier.height(12.dp))

            // Room info
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Phòng",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = booking.roomName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Số khách",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${booking.numGuests} người",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dates
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Check-in",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = booking.checkInDate?.let { DateUtils.formatDate(it) } ?: "N/A",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Check-out",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = booking.checkOutDate?.let { DateUtils.formatDate(it) } ?: "N/A",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Note if exists
            booking.note?.takeIf { it.isNotBlank() }?.let { note ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ghi chú: $note",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Status message for PENDING
            if (statusUpper == "PENDING") {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(DesignSystem.Spacing.listItemSpacing),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.HourglassTop,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Đang chờ hành chính phê duyệt. Bạn có thể chỉnh sửa hoặc hủy.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }

            // Action buttons
            if (canEdit || canCancel || hasOrder) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (canEdit) {
                        OutlinedButton(
                            onClick = onEditClick,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Sửa")
                        }
                    }
                    
                    if (hasOrder) {
                        OutlinedButton(
                            onClick = onViewOrderClick,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Default.Receipt,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Xem hóa đơn")
                        }
                    }
                    
                    if (canCancel) {
                        Button(
                            onClick = onCancelClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Default.Cancel,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Hủy")
                        }
                    }
                }
            }
        }
    }
}

