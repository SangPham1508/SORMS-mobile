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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.sorms_app.data.models.RoomData
import com.example.sorms_app.presentation.theme.DesignSystem
import com.example.sorms_app.presentation.utils.DateUtils
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Multi-step booking modal với 3 bước:
 * 1. Thông tin cá nhân
 * 2. Kiểm tra khuôn mặt
 * 3. Xác nhận đặt phòng
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiStepBookingModal(
    room: RoomData?,
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onBookingSuccess: () -> Unit,
    onFaceRegisterClick: () -> Unit,
    checkFaceStatus: suspend () -> kotlin.Result<Boolean>, // Returns true if face is registered
    createBooking: suspend (
        Long,    // roomId
        String,  // checkInDate (yyyy-MM-dd)
        String,  // checkInTime (HH:mm)
        String,  // checkOutDate (yyyy-MM-dd)
        String,  // checkOutTime (HH:mm)
        Int,     // numGuests
        String?  // note
    ) -> kotlin.Result<Unit>
) {
    if (!isOpen || room == null) return

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var currentStep by remember { mutableStateOf(1) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var faceStatus by remember { mutableStateOf<FaceStatus>(FaceStatus.Loading) }

    // Personal info
    var fullName by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var cccd by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var showContactFields by remember { mutableStateOf(true) }

    // Booking dates/times
    var checkInDate by remember { mutableStateOf<Date?>(null) }
    var checkInTime by remember { mutableStateOf("14:00") }
    var checkOutDate by remember { mutableStateOf<Date?>(null) }
    var checkOutTime by remember { mutableStateOf("12:00") }
    var numGuests by remember { mutableStateOf(1) }
    var note by remember { mutableStateOf("") }

    // Date pickers
    val calendar = Calendar.getInstance()
    val checkInDatePicker = remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val cal = Calendar.getInstance()
                cal.set(year, month, day)
                checkInDate = cal.time
                // Auto-set checkout date to next day
                if (checkOutDate == null || checkOutDate!! <= cal.time) {
                    cal.add(Calendar.DAY_OF_MONTH, 1)
                    checkOutDate = cal.time
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis() - 1000
        }
    }

    val checkOutDatePicker = remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val cal = Calendar.getInstance()
                cal.set(year, month, day)
                checkOutDate = cal.time
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis() - 1000
        }
    }

    // Time pickers
    val checkInTimePicker = remember {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                checkInTime = String.format("%02d:%02d", hourOfDay, minute)
            },
            14,
            0,
            true
        )
    }

    val checkOutTimePicker = remember {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                checkOutTime = String.format("%02d:%02d", hourOfDay, minute)
            },
            12,
            0,
            true
        )
    }

    // Check face status when step 2 is reached
    LaunchedEffect(currentStep) {
        if (currentStep == 2) {
            faceStatus = FaceStatus.Loading
            checkFaceStatus().fold(
                onSuccess = { registered ->
                    faceStatus = if (registered) FaceStatus.Registered else FaceStatus.NotRegistered
                },
                onFailure = {
                    faceStatus = FaceStatus.Error(it.message ?: "Không thể kiểm tra trạng thái khuôn mặt")
                }
            )
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
                        text = "Đặt phòng",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Đóng")
                    }
                }

                Spacer(modifier = Modifier.height(DesignSystem.Spacing.md))

                ProgressIndicator(
                    currentStep = currentStep,
                    totalSteps = 3,
                    completedSteps = emptySet(),
                    stepLabels = listOf("Thông tin", "Khuôn mặt", "Xác nhận"),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(DesignSystem.Spacing.lg))

                // Error
                errorMessage?.let { error ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
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
                        1 -> Step1PersonalInfo(
                            fullName = fullName,
                            onFullNameChange = { fullName = it },
                            dateOfBirth = dateOfBirth,
                            onDateOfBirthChange = { dateOfBirth = it },
                            cccd = cccd,
                            onCccdChange = { cccd = it },
                            phone = phone,
                            onPhoneChange = { phone = it },
                            email = email,
                            onEmailChange = { email = it },
                            showContactFields = showContactFields,
                            checkInDate = checkInDate,
                            checkInTime = checkInTime,
                            checkOutDate = checkOutDate,
                            checkOutTime = checkOutTime,
                            numGuests = numGuests,
                            onNumGuestsChange = { numGuests = it },
                            note = note,
                            onNoteChange = { note = it },
                            onCheckInDateClick = { checkInDatePicker.show() },
                            onCheckInTimeClick = { checkInTimePicker.show() },
                            onCheckOutDateClick = { checkOutDatePicker.show() },
                            onCheckOutTimeClick = { checkOutTimePicker.show() },
                            room = room
                        )

                        2 -> Step2FaceStatus(
                            faceStatus = faceStatus,
                            onFaceRegisterClick = onFaceRegisterClick,
                            onContinue = {
                                if (faceStatus == FaceStatus.Registered) {
                                    currentStep = 3
                                } else {
                                    errorMessage = "Bạn cần đăng ký khuôn mặt trước khi tiếp tục"
                                }
                            }
                        )

                        3 -> Step3ConfirmBooking(
                            room = room,
                            checkInDate = checkInDate,
                            checkInTime = checkInTime,
                            checkOutDate = checkOutDate,
                            checkOutTime = checkOutTime,
                            numGuests = numGuests,
                            note = note,
                            isLoading = isLoading,
                            onConfirm = {
                                if (checkInDate == null || checkOutDate == null) {
                                    errorMessage = "Vui lòng chọn ngày check-in và check-out"
                                    return@Step3ConfirmBooking
                                }
                                if (checkOutDate!! <= checkInDate!!) {
                                    errorMessage = "Ngày check-out phải sau ngày check-in"
                                    return@Step3ConfirmBooking
                                }
                                if (faceStatus != FaceStatus.Registered) {
                                    errorMessage = "Bạn cần đăng ký khuôn mặt trước khi đặt phòng"
                                    currentStep = 2
                                    return@Step3ConfirmBooking
                                }

                                isLoading = true
                                errorMessage = null

                                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                val checkInDateStr = dateFormat.format(checkInDate!!)
                                val checkOutDateStr = dateFormat.format(checkOutDate!!)

                                scope.launch {
                                    val result = createBooking(
                                        room.id,
                                        checkInDateStr,
                                        checkInTime,
                                        checkOutDateStr,
                                        checkOutTime,
                                        numGuests,
                                        note.takeIf { it.isNotBlank() }
                                    )

                                    result.fold(
                                        onSuccess = {
                                            isLoading = false
                                            Toast.makeText(context, "Đặt phòng thành công!", Toast.LENGTH_LONG).show()
                                            onBookingSuccess()
                                            onDismiss()
                                        },
                                        onFailure = { e ->
                                            isLoading = false
                                            errorMessage = e.message ?: "Không thể đặt phòng"
                                        }
                                    )
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(DesignSystem.Spacing.md))

                // Bottom nav buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.elementSpacing)
                ) {
                    if (currentStep > 1) {
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

                    Button(
                        onClick = {
                            when (currentStep) {
                                1 -> {
                                    if (fullName.isBlank()) {
                                        errorMessage = "Vui lòng nhập họ tên"
                                        return@Button
                                    }
                                    if (cccd.isBlank()) {
                                        errorMessage = "Vui lòng nhập số CCCD/CMND"
                                        return@Button
                                    }
                                    if (checkInDate == null || checkOutDate == null) {
                                        errorMessage = "Vui lòng chọn ngày check-in và check-out"
                                        return@Button
                                    }
                                    errorMessage = null
                                    currentStep = 2
                                }
                                2 -> {
                                    // handled in Step2FaceStatus
                                }
                                else -> {}
                            }
                        },
                        enabled = !isLoading && currentStep == 1,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Tiếp theo")
                    }
                }
            }
        }
    }
}

// Step 1
@Composable
private fun Step1PersonalInfo(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    dateOfBirth: String,
    onDateOfBirthChange: (String) -> Unit,
    cccd: String,
    onCccdChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    showContactFields: Boolean,
    checkInDate: Date?,
    checkInTime: String,
    checkOutDate: Date?,
    checkOutTime: String,
    numGuests: Int,
    onNumGuestsChange: (Int) -> Unit,
    note: String,
    onNoteChange: (String) -> Unit,
    onCheckInDateClick: () -> Unit,
    onCheckInTimeClick: () -> Unit,
    onCheckOutDateClick: () -> Unit,
    onCheckOutTimeClick: () -> Unit,
    room: RoomData
) {
    Column(verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.md)) {
        Text("Thông tin cá nhân", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)

        SormsCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(DesignSystem.Spacing.cardContentPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.MeetingRoom, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(room.name ?: room.code ?: "Phòng #${room.id}", style = MaterialTheme.typography.titleSmall)
                    Text(
                        "Tầng ${room.floor ?: "N/A"} • ${room.capacity ?: "N/A"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        OutlinedTextField(
            value = fullName,
            onValueChange = onFullNameChange,
            label = { Text("Họ tên *") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
        )

        OutlinedTextField(
            value = dateOfBirth,
            onValueChange = onDateOfBirthChange,
            label = { Text("Ngày sinh") },
            placeholder = { Text("YYYY-MM-DD") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) }
        )

        OutlinedTextField(
            value = cccd,
            onValueChange = { v -> onCccdChange(v.filter { it.isDigit() }) },
            label = { Text("Số CCCD/CMND *") },
            placeholder = { Text("9 hoặc 12 chữ số") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
            maxLines = 1
        )

        if (showContactFields) {
            OutlinedTextField(
                value = phone,
                onValueChange = onPhoneChange,
                label = { Text("Số điện thoại") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) }
            )

            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
            )
        }

        Divider(modifier = Modifier.padding(vertical = DesignSystem.Spacing.sm))

        Text("Thời gian đặt phòng", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.elementSpacing)
        ) {
            OutlinedTextField(
                value = checkInDate?.let { DateUtils.formatDate(it) } ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Ngày check-in *") },
                modifier = Modifier
                    .weight(1f)
                    .clickable { onCheckInDateClick() },
                trailingIcon = {
                    IconButton(onClick = onCheckInDateClick) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null)
                    }
                }
            )

            OutlinedTextField(
                value = checkInTime,
                onValueChange = {},
                readOnly = true,
                label = { Text("Giờ") },
                modifier = Modifier
                    .weight(1f)
                    .clickable { onCheckInTimeClick() },
                trailingIcon = {
                    IconButton(onClick = onCheckInTimeClick) {
                        Icon(Icons.Default.Schedule, contentDescription = null)
                    }
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.elementSpacing)
        ) {
            OutlinedTextField(
                value = checkOutDate?.let { DateUtils.formatDate(it) } ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Ngày check-out *") },
                modifier = Modifier
                    .weight(1f)
                    .clickable { onCheckOutDateClick() },
                trailingIcon = {
                    IconButton(onClick = onCheckOutDateClick) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null)
                    }
                }
            )

            OutlinedTextField(
                value = checkOutTime,
                onValueChange = {},
                readOnly = true,
                label = { Text("Giờ") },
                modifier = Modifier
                    .weight(1f)
                    .clickable { onCheckOutTimeClick() },
                trailingIcon = {
                    IconButton(onClick = onCheckOutTimeClick) {
                        Icon(Icons.Default.Schedule, contentDescription = null)
                    }
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Số khách:", style = MaterialTheme.typography.bodyMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { if (numGuests > 1) onNumGuestsChange(numGuests - 1) }) {
                    Icon(Icons.Default.Remove, contentDescription = "Giảm")
                }
                Text(
                    text = numGuests.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                IconButton(onClick = { onNumGuestsChange(numGuests + 1) }) {
                    Icon(Icons.Default.Add, contentDescription = "Tăng")
                }
            }
        }

        OutlinedTextField(
            value = note,
            onValueChange = onNoteChange,
            label = { Text("Ghi chú (tùy chọn)") },
            placeholder = { Text("Ví dụ: Cần phòng yên tĩnh...") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 3
        )
    }
}

// Step 2
@Composable
private fun Step2FaceStatus(
    faceStatus: FaceStatus,
    onFaceRegisterClick: () -> Unit,
    onContinue: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.md)) {
        Text("Trạng thái khuôn mặt", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)

        Card(
            colors = CardDefaults.cardColors(
                containerColor = when (faceStatus) {
                    is FaceStatus.Registered -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    is FaceStatus.NotRegistered -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    is FaceStatus.Error -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    is FaceStatus.Loading -> MaterialTheme.colorScheme.surfaceVariant
                }
            )
        ) {
            Column(modifier = Modifier.padding(DesignSystem.Spacing.cardContentPadding)) {
                when (faceStatus) {
                    is FaceStatus.Loading -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("Đang kiểm tra dữ liệu khuôn mặt...")
                        }
                    }
                    is FaceStatus.Registered -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Đã có dữ liệu khuôn mặt",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF10B981)
                                )
                                Text(
                                    text = "Bạn có thể tiếp tục hoặc đăng ký lại nếu muốn cập nhật.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            SormsBadge(text = "Đã đăng ký", tone = BadgeTone.Success)
                        }
                    }
                    is FaceStatus.NotRegistered -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Chưa có dữ liệu khuôn mặt",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = "Cần đăng ký trước khi hoàn tất đặt phòng.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            SormsBadge(text = "Chưa đăng ký", tone = BadgeTone.Warning)
                        }
                    }
                    is FaceStatus.Error -> {
                        Text(faceStatus.message, fontSize = 14.sp, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        if (faceStatus is FaceStatus.Registered) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.elementSpacing)
            ) {
                OutlinedButton(onClick = onFaceRegisterClick, modifier = Modifier.weight(1f)) {
                    Text("Đăng ký lại")
                }
                Button(onClick = onContinue, modifier = Modifier.weight(1f)) {
                    Text("Tiếp tục")
                }
            }
        } else if (faceStatus is FaceStatus.NotRegistered) {
            Button(onClick = onFaceRegisterClick, modifier = Modifier.fillMaxWidth()) {
                Text("Đăng ký khuôn mặt")
            }
        }
    }
}

// Step 3
@Composable
private fun Step3ConfirmBooking(
    room: RoomData,
    checkInDate: Date?,
    checkInTime: String,
    checkOutDate: Date?,
    checkOutTime: String,
    numGuests: Int,
    note: String,
    isLoading: Boolean,
    onConfirm: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.md)) {
        Text("Xác nhận đặt phòng", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)

        SormsCard {
            Column(
                modifier = Modifier.padding(DesignSystem.Spacing.cardContentPadding),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Tóm tắt đặt phòng", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Phòng:")
                    Text(room.name ?: room.code ?: "Phòng #${room.id}", fontWeight = FontWeight.Medium)
                }

                if (checkInDate != null && checkOutDate != null) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Check-in:")
                        Text("${DateUtils.formatDate(checkInDate)} $checkInTime", fontWeight = FontWeight.Medium)
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Check-out:")
                        Text("${DateUtils.formatDate(checkOutDate)} $checkOutTime", fontWeight = FontWeight.Medium)
                    }
                }

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Số khách:")
                    Text(numGuests.toString(), fontWeight = FontWeight.Medium)
                }

                if (note.isNotBlank()) {
                    Text("Ghi chú: $note", fontSize = 13.sp)
                }
            }
        }

        Button(
            onClick = onConfirm,
            enabled = !isLoading && checkInDate != null && checkOutDate != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(if (isLoading) "Đang xử lý..." else "Xác nhận đặt phòng")
        }
    }
}

sealed class FaceStatus {
    object Loading : FaceStatus()
    object Registered : FaceStatus()
    object NotRegistered : FaceStatus()
    data class Error(val message: String) : FaceStatus()
}
