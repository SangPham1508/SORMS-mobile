package com.example.sorms_app.presentation.screens.user.services

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sorms_app.domain.model.Booking
import com.example.sorms_app.presentation.components.SormsBadge
import com.example.sorms_app.presentation.components.SormsButton
import com.example.sorms_app.presentation.components.SormsCard
import com.example.sorms_app.presentation.viewmodel.ServiceViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceRequestScreen(
    serviceId: String,
    viewModel: ServiceViewModel,
    onNavigateBack: () -> Unit,
    onSubmissionSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("vi", "VN")) }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    // Find the service details from the list
    val service = uiState.services.find { it.id == serviceId }
    
    // Form states
    var notes by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf(1) }
    var selectedBooking by remember { mutableStateOf<Booking?>(null) }
    var selectedDate by remember { mutableStateOf<Date?>(null) }
    var showBookingDropdown by remember { mutableStateOf(false) }

    // Load bookings on first composition
    LaunchedEffect(Unit) {
        viewModel.loadUserBookings()
    }

    // Handle submission success
    LaunchedEffect(uiState.submissionSuccess) {
        if (uiState.submissionSuccess) {
            Toast.makeText(context, "Yêu cầu dịch vụ đã được gửi thành công!", Toast.LENGTH_SHORT).show()
            viewModel.resetSubmissionState()
            onSubmissionSuccess()
        }
    }

    // Handle submission error
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, "Lỗi: $it", Toast.LENGTH_LONG).show()
            viewModel.resetSubmissionState()
        }
    }

    // Date picker dialog
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val calendar = Calendar.getInstance()
                calendar.set(year, month, day)
                selectedDate = calendar.time
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
            TopAppBar(
                title = { Text("Yêu cầu dịch vụ") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (service == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Không tìm thấy thông tin dịch vụ.")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Service Info Card
                SormsCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = service.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Giá: ${currencyFormat.format(service.unitPrice)}/${service.unitName}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Booking Selection
                Text(
                    text = "Chọn đặt phòng *",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
                
                ExposedDropdownMenuBox(
                    expanded = showBookingDropdown,
                    onExpandedChange = { showBookingDropdown = !showBookingDropdown }
                ) {
                    OutlinedTextField(
                        value = selectedBooking?.let { 
                            "${it.code} - ${it.roomName}" 
                        } ?: "Chọn đặt phòng",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showBookingDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = showBookingDropdown,
                        onDismissRequest = { showBookingDropdown = false }
                    ) {
                        if (uiState.userBookings.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Không có đặt phòng nào") },
                                onClick = { showBookingDropdown = false }
                            )
                        } else {
                            uiState.userBookings
                                .filter { it.status.uppercase() in listOf("APPROVED", "CHECKED_IN") }
                                .forEach { booking ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(
                                                    text = "${booking.code} - ${booking.roomName}",
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Text(
                                                    text = "${dateFormat.format(booking.checkInDate)} - ${dateFormat.format(booking.checkOutDate)}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        },
                                        onClick = {
                                            selectedBooking = booking
                                            showBookingDropdown = false
                                        }
                                    )
                                }
                        }
                    }
                }

                if (uiState.userBookings.none { it.status.uppercase() in listOf("APPROVED", "CHECKED_IN") }) {
                    Text(
                        text = "⚠️ Bạn cần có đặt phòng đã được duyệt để đặt dịch vụ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                // Date Selection
                Text(
                    text = "Ngày mong muốn *",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
                
                OutlinedTextField(
                    value = selectedDate?.let { dateFormat.format(it) } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Chọn ngày") },
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Chọn ngày")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() }
                )

                // Quantity Selection
                Text(
                    text = "Số lượng",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IconButton(
                        onClick = { if (quantity > 1) quantity-- },
                        enabled = quantity > 1
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Giảm")
                    }
                    
                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    IconButton(onClick = { quantity++ }) {
                        Icon(Icons.Default.Add, contentDescription = "Tăng")
                    }
                    
                    Spacer(Modifier.weight(1f))
                    
                    Text(
                        text = "Tổng: ${currencyFormat.format(service.unitPrice * quantity)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Notes
                Text(
                    text = "Ghi chú",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
                
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text("Ví dụ: Cần dọn phòng vào buổi sáng, thay chăn ga...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    maxLines = 6
                )

                Spacer(Modifier.height(16.dp))

                // Submit Button
                SormsButton(
                    onClick = {
                        if (selectedBooking == null) {
                            Toast.makeText(context, "Vui lòng chọn đặt phòng", Toast.LENGTH_SHORT).show()
                            return@SormsButton
                        }
                        if (selectedDate == null) {
                            Toast.makeText(context, "Vui lòng chọn ngày", Toast.LENGTH_SHORT).show()
                            return@SormsButton
                        }
                        
                        viewModel.createServiceRequestWithBooking(
                            serviceId = service.id,
                            bookingId = selectedBooking!!.id,
                            quantity = quantity,
                            serviceDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                                .format(selectedDate!!),
                            notes = notes
                        )
                    },
                    text = if (uiState.isSubmitting) "Đang gửi..." else "Gửi yêu cầu",
                    enabled = !uiState.isSubmitting && selectedBooking != null && selectedDate != null,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
