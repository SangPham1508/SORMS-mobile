package com.example.sorms_app.presentation.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.sorms_app.domain.model.Booking
import com.example.sorms_app.domain.model.Service
import com.example.sorms_app.presentation.theme.DesignSystem
import com.example.sorms_app.presentation.utils.FormatUtils
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Multi-step service order modal với 4 bước:
 * 1. Create cart (chọn booking)
 * 2. Add item (chọn service, quantity, date/time)
 * 3. Assign staff (chọn nhân viên, note)
 * 4. Success
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiStepServiceOrderModal(
    service: Service?,
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onOrderSuccess: () -> Unit,
    bookings: List<Booking>,
    staffOptions: List<StaffOption>,
    isLoadingBookings: Boolean = false,
    isLoadingStaff: Boolean = false,
    createOrderCart: suspend (bookingId: Long, note: String?) -> Result<Long>, // Returns orderId
    addOrderItem: suspend (orderId: Long, serviceId: Long, quantity: Int) -> Result<Unit>,
    createServiceOrder: suspend (
        bookingId: Long,
        orderId: Long,
        serviceId: Long,
        quantity: Int,
        assignedStaffId: Long,
        requestedBy: String,
        serviceTime: String,
        note: String?
    ) -> Result<Unit>
) {
    if (!isOpen || service == null) return

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var currentStep by remember { mutableStateOf(1) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var completedSteps by remember { mutableStateOf<Set<Int>>(emptySet()) }

    // Step 1: Booking selection
    var selectedBookingId by remember { mutableStateOf<Long?>(null) }
    var cartNote by remember { mutableStateOf("") }
    var orderId by remember { mutableStateOf<Long?>(null) }

    // Step 2: Service details
    var quantity by remember { mutableStateOf(1) }
    var serviceDate by remember { mutableStateOf<Date?>(null) }
    var serviceTime by remember { mutableStateOf("12:00") }

    // Step 3: Staff assignment
    var selectedStaffId by remember { mutableStateOf<Long?>(null) }
    var orderNote by remember { mutableStateOf("") }

    // Step 4: Success (no state needed)

    // Date picker
    val calendar = Calendar.getInstance()
    val datePicker = remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val cal = Calendar.getInstance()
                cal.set(year, month, day)
                serviceDate = cal.time
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis() - 1000
        }
    }

    // Time picker
    val timePicker = remember {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                serviceTime = String.format("%02d:%02d", hourOfDay, minute)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
    }

    // Reset when modal closes
    LaunchedEffect(isOpen) {
        if (!isOpen) {
            currentStep = 1
            errorMessage = null
            completedSteps = emptySet()
            selectedBookingId = null
            orderId = null
            quantity = 1
            serviceDate = null
            serviceTime = "12:00"
            selectedStaffId = null
            cartNote = ""
            orderNote = ""
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(DesignSystem.Spacing.cardPadding)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Đơn đặt dịch vụ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Đóng")
                    }
                }

                Spacer(modifier = Modifier.height(DesignSystem.Spacing.md))

                // Progress indicator
                ProgressIndicator(
                    currentStep = currentStep,
                    totalSteps = 4,
                    completedSteps = completedSteps,
                    stepLabels = listOf("Tạo đơn", "Thêm dịch vụ", "Gán nhân viên", "Hoàn tất"),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(DesignSystem.Spacing.lg))

                // Error message
                errorMessage?.let { error ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(DesignSystem.Spacing.cardContentPadding),
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(DesignSystem.Spacing.md))
                }

                // Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    when (currentStep) {
                        1 -> Step1CreateCart(
                            bookings = bookings,
                            isLoading = isLoadingBookings,
                            selectedBookingId = selectedBookingId,
                            onBookingSelected = { selectedBookingId = it },
                            note = cartNote,
                            onNoteChange = { cartNote = it },
                            service = service
                        )
                        2 -> Step2AddItem(
                            service = service,
                            quantity = quantity,
                            onQuantityChange = { quantity = it },
                            serviceDate = serviceDate,
                            serviceTime = serviceTime,
                            onDateClick = { datePicker.show() },
                            onTimeClick = { timePicker.show() }
                        )
                        3 -> Step3AssignStaff(
                            staffOptions = staffOptions,
                            isLoading = isLoadingStaff,
                            selectedStaffId = selectedStaffId,
                            onStaffSelected = { selectedStaffId = it },
                            note = orderNote,
                            onNoteChange = { orderNote = it }
                        )
                        4 -> Step4Success()
                    }
                }

                Spacer(modifier = Modifier.height(DesignSystem.Spacing.md))

                // Navigation buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.elementSpacing)
                ) {
                    if (currentStep > 1 && currentStep < 4) {
                        OutlinedButton(
                            onClick = { currentStep-- },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading
                        ) {
                            Text("Quay lại")
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    if (currentStep < 4) {
                        Button(
                            onClick = {
                                when (currentStep) {
                                    1 -> {
                                        if (selectedBookingId == null) {
                                            errorMessage = "Vui lòng chọn phòng đang ở"
                                            return@Button
                                        }
                                        if (bookings.isEmpty()) {
                                            errorMessage = "Bạn cần có booking đang check-in để đặt dịch vụ"
                                            return@Button
                                        }
                                        isLoading = true
                                        errorMessage = null
                                        scope.launch {
                                            val result = createOrderCart(selectedBookingId!!, cartNote.takeIf { it.isNotBlank() })
                                            result.fold(
                                                onSuccess = { orderIdValue ->
                                                    orderId = orderIdValue
                                                    completedSteps = completedSteps + 1
                                                    currentStep = 2
                                                    isLoading = false
                                                },
                                                onFailure = { e ->
                                                    errorMessage = e.message ?: "Không thể tạo đơn hàng"
                                                    isLoading = false
                                                }
                                            )
                                        }
                                    }
                                    2 -> {
                                        if (serviceDate == null) {
                                            errorMessage = "Vui lòng chọn ngày sử dụng dịch vụ"
                                            return@Button
                                        }
                                        isLoading = true
                                        errorMessage = null
                                        scope.launch {
                                            val result = addOrderItem(orderId!!, service.id.toLongOrNull() ?: 0L, quantity)
                                            result.fold(
                                                onSuccess = {
                                                    completedSteps = completedSteps + 2
                                                    currentStep = 3
                                                    isLoading = false
                                                },
                                                onFailure = { e ->
                                                    errorMessage = e.message ?: "Không thể thêm dịch vụ"
                                                    isLoading = false
                                                }
                                            )
                                        }
                                    }
                                    3 -> {
                                        if (selectedStaffId == null) {
                                            errorMessage = "Vui lòng chọn nhân viên"
                                            return@Button
                                        }
                                        if (staffOptions.isEmpty()) {
                                            errorMessage = "Chưa có nhân viên hoạt động"
                                            return@Button
                                        }
                                        if (serviceDate == null) {
                                            errorMessage = "Vui lòng chọn ngày sử dụng dịch vụ"
                                            return@Button
                                        }
                                        isLoading = true
                                        errorMessage = null

                                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                                        val serviceDateTime = "${dateFormat.format(serviceDate!!)}T${serviceTime}:00"

                                        val requestedBy = com.example.sorms_app.data.datasource.local.AuthSession.accountId
                                            ?: return@Button.also {
                                                errorMessage = "Không tìm thấy thông tin người dùng"
                                                isLoading = false
                                            }

                                        scope.launch {
                                            val result = createServiceOrder(
                                                selectedBookingId!!,
                                                orderId!!,
                                                service.id.toLongOrNull() ?: 0L,
                                                quantity,
                                                selectedStaffId!!,
                                                requestedBy,
                                                serviceDateTime,
                                                orderNote.takeIf { it.isNotBlank() }
                                            )
                                            result.fold(
                                                onSuccess = {
                                                    completedSteps = completedSteps + 3
                                                    currentStep = 4
                                                    isLoading = false
                                                },
                                                onFailure = { e ->
                                                    errorMessage = e.message ?: "Không thể tạo đơn hàng"
                                                    isLoading = false
                                                }
                                            )
                                        }
                                    }
                                }
                            },
                            enabled = !isLoading && when (currentStep) {
                                1 -> selectedBookingId != null && bookings.isNotEmpty()
                                2 -> serviceDate != null && orderId != null
                                3 -> selectedStaffId != null && staffOptions.isNotEmpty() && serviceDate != null
                                else -> false
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(Modifier.width(8.dp))
                            }
                            Text(if (isLoading) "Đang xử lý..." else "Tiếp theo")
                        }
                    } else {
                        // Step 4: Success
                        Button(
                            onClick = {
                                onOrderSuccess()
                                onDismiss()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Xem đơn hàng")
                        }
                    }
                }
            }
        }
    }
}

// Step 1: Create Cart
@Composable
private fun Step1CreateCart(
    bookings: List<Booking>,
    isLoading: Boolean,
    selectedBookingId: Long?,
    onBookingSelected: (Long) -> Unit,
    note: String,
    onNoteChange: (String) -> Unit,
    service: Service
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.md)
    ) {
        Text(
            text = "Bước 1: Tạo đơn hàng",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        SormsCard {
            Column(
                modifier = Modifier.padding(DesignSystem.Spacing.cardContentPadding),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Dịch vụ đã chọn:", fontSize = 14.sp)
                    SormsBadge(
                        text = "Đã chọn",
                        tone = BadgeTone.Default
                    )
                }
                Text(
                    text = service.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (bookings.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(DesignSystem.Spacing.cardContentPadding)
                ) {
                    Text(
                        text = "Chưa có phòng đang ở",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "Bạn cần có booking đang check-in để đặt dịch vụ",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        } else {
            Text(
                text = "Phòng đang ở *",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            bookings.forEach { booking ->
                Card(
                    onClick = { onBookingSelected(booking.id) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedBookingId == booking.id) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(DesignSystem.Spacing.cardContentPadding),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = booking.roomName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Check-in: ${com.example.sorms_app.presentation.utils.DateUtils.formatDateShort(booking.checkInDate)}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (selectedBookingId == booking.id) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        OutlinedTextField(
            value = note,
            onValueChange = onNoteChange,
            label = { Text("Ghi chú (tùy chọn)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 3
        )
    }
}

// Step 2: Add Item
@Composable
private fun Step2AddItem(
    service: Service,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    serviceDate: Date?,
    serviceTime: String,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.md)
    ) {
        Text(
            text = "Bước 2: Thêm dịch vụ",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        SormsCard {
            Column(
                modifier = Modifier.padding(DesignSystem.Spacing.cardContentPadding),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = service.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${FormatUtils.formatCurrency(service.unitPrice)}/${service.unitName}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Số lượng *", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { if (quantity > 1) onQuantityChange(quantity - 1) }) {
                    Icon(Icons.Default.Remove, contentDescription = "Giảm")
                }
                Text(
                    text = quantity.toString(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                IconButton(onClick = { onQuantityChange(quantity + 1) }) {
                    Icon(Icons.Default.Add, contentDescription = "Tăng")
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.elementSpacing)
        ) {
            OutlinedTextField(
                value = serviceDate?.let { com.example.sorms_app.presentation.utils.DateUtils.formatDate(it) } ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Ngày sử dụng *") },
                modifier = Modifier
                    .weight(1f)
                    .clickable { onDateClick() },
                trailingIcon = {
                    IconButton(onClick = onDateClick) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null)
                    }
                }
            )

            OutlinedTextField(
                value = serviceTime,
                onValueChange = {},
                readOnly = true,
                label = { Text("Giờ *") },
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTimeClick() },
                trailingIcon = {
                    IconButton(onClick = onTimeClick) {
                        Icon(Icons.Default.Schedule, contentDescription = null)
                    }
                }
            )
        }

        // Total price
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(DesignSystem.Spacing.cardContentPadding),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tổng cộng", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    text = FormatUtils.formatCurrency(service.unitPrice * quantity),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// Step 3: Assign Staff
@Composable
private fun Step3AssignStaff(
    staffOptions: List<StaffOption>,
    isLoading: Boolean,
    selectedStaffId: Long?,
    onStaffSelected: (Long) -> Unit,
    note: String,
    onNoteChange: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.md)
    ) {
        Text(
            text = "Bước 3: Gán nhân viên",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (staffOptions.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(DesignSystem.Spacing.cardContentPadding)
                ) {
                    Text(
                        text = "Chưa có nhân viên hoạt động",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "Vui lòng liên hệ lễ tân để được hỗ trợ",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        } else {
            Text(
                text = "Nhân viên thực hiện *",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            staffOptions.forEach { staff ->
                Card(
                    onClick = { onStaffSelected(staff.id) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedStaffId == staff.id) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(DesignSystem.Spacing.cardContentPadding),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = staff.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        if (selectedStaffId == staff.id) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        OutlinedTextField(
            value = note,
            onValueChange = onNoteChange,
            label = { Text("Ghi chú (tùy chọn)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5,
            placeholder = { Text("Nhập yêu cầu đặc biệt hoặc ghi chú...") }
        )
    }
}

// Step 4: Success
@Composable
private fun Step4Success() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.md)
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFD1FAE5) // Light green
            )
        ) {
            Column(
                modifier = Modifier.padding(DesignSystem.Spacing.cardContentPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color(0xFF10B981)
                )
                Text(
                    text = "Đơn hàng đã được tạo thành công!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF065F46)
                )
                Text(
                    text = "Đơn hàng đã được gán cho nhân viên và đang chờ xác nhận.",
                    fontSize = 14.sp,
                    color = Color(0xFF047857),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

data class StaffOption(
    val id: Long,
    val name: String
)

