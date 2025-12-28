package com.example.sorms_app.presentation.screens.user.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Surface
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sorms_app.domain.model.Booking
import com.example.sorms_app.presentation.components.*
import com.example.sorms_app.presentation.components.QRCodeModal
import com.example.sorms_app.presentation.theme.DesignSystem
import com.example.sorms_app.presentation.theme.Green500
import com.example.sorms_app.presentation.theme.SORMS_appTheme
import com.example.sorms_app.presentation.theme.Yellow500
import com.example.sorms_app.presentation.theme.Yellow800
import com.example.sorms_app.presentation.utils.DateUtils
import com.example.sorms_app.presentation.utils.StatusUtils
import com.example.sorms_app.presentation.viewmodel.UserDashboardViewModel
import java.util.Calendar
import java.util.Date
import androidx.compose.material.icons.filled.QrCode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboardScreen(
    onNavigateToRooms: () -> Unit,
    onNavigateToServices: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToFaceRegister: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UserDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showQRCodeModal by remember { mutableStateOf(false) }
    var selectedBookingForQR by remember { mutableStateOf<Booking?>(null) }
    
    // Load data when screen is first composed
    LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
    LazyColumn(
            modifier = Modifier
            .fillMaxSize()
            .padding(DesignSystem.Spacing.screenHorizontal),
        verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.sectionSpacing)  // Tăng spacing giữa sections
    ) {
        // Welcome Section
        item {
            WelcomeSection(
                userName = uiState.userName,
                onBookRoom = onNavigateToRooms,
                onOrderService = onNavigateToServices
            )
        }

        // Active Bookings Section (đồng bộ với web: hiển thị list thay vì single booking)
        item {
            ActiveBookingsSection(
                activeBookings = uiState.activeBookings,
                onViewQR = { booking ->
                    selectedBookingForQR = booking
                    showQRCodeModal = true
                },
                onNavigateToHistory = onNavigateToHistory
            )
        }

        // Pending Bookings Section (đồng bộ với web)
        if (uiState.pendingBookings.isNotEmpty()) {
            item {
                PendingBookingsSection(
                    pendingBookings = uiState.pendingBookings,
                    onNavigateToHistory = onNavigateToHistory
                )
            }
        }

        // Quick Actions
        item {
            QuickActionsSection(
                onNavigateToRooms = onNavigateToRooms,
                onNavigateToServices = onNavigateToServices,
                onNavigateToOrders = onNavigateToOrders,
                onNavigateToHistory = onNavigateToHistory
            )
        }

        // Summary Cards
        item {
            SummarySection(
                activeBookings = uiState.activeBookingsCount,
                pendingBookings = uiState.pendingBookingsCount,  // Đồng bộ với web: thêm pending count
                serviceOrders = uiState.serviceOrdersCount,
                unpaidOrders = uiState.unpaidOrdersCount,
                faceRegistrationStatus = uiState.faceRegistrationStatus,  // Đồng bộ với web: thêm face status
                onFaceRegisterClick = onNavigateToFaceRegister
            )
        }
        }
        
        // QR Code Modal - đồng bộ với web
        selectedBookingForQR?.let { booking ->
            if ((booking.status.equals("APPROVED", ignoreCase = true) || 
                 booking.status.equals("CHECKED_IN", ignoreCase = true)) && 
                !booking.qrImageUrl.isNullOrBlank()) {
                QRCodeModal(
                    open = showQRCodeModal,
                    onClose = { 
                        showQRCodeModal = false
                        selectedBookingForQR = null
                    },
                    qrImageUrl = booking.qrImageUrl,
                    bookingCode = booking.code,
                    checkInDate = booking.checkInDate?.let { DateUtils.formatDateShort(it) },
                    checkOutDate = booking.checkOutDate?.let { DateUtils.formatDateShort(it) }
            )
        }
        }
    }
}

@Composable
private fun WelcomeSection(
    userName: String,
    onBookRoom: () -> Unit,
    onOrderService: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,  // Giảm từ 8dp để tối giản
                shape = RoundedCornerShape(20.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)  // Giảm alpha
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),  // Giảm alpha từ 0.3f
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(DesignSystem.Spacing.cardPadding)  // Sử dụng DesignSystem spacing
        ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Chào mừng trở lại,",
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),  // Tăng contrast
                            fontWeight = FontWeight.Medium
            )
            
            Text(
                text = userName,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 8.dp)  // Tăng spacing
            )
            
            Text(
                text = "Quản lý nhanh đặt phòng, dịch vụ và hóa đơn của bạn.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),  // Tăng contrast từ 0.7f
                lineHeight = 20.sp
            )
                    }
                    // Loại bỏ decorative icon để tối giản hóa
                }
            
                Spacer(modifier = Modifier.height(DesignSystem.Spacing.lg))  // Tăng spacing từ 20dp lên 24dp
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.elementSpacing)  // Sử dụng DesignSystem spacing
            ) {
                    // Note: SormsButton doesn't support icons yet, using Button for now
                    // TODO: Add icon support to SormsButton
                    Button(
                    onClick = onBookRoom,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)  // Giảm elevation để tối giản
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Đặt phòng", fontWeight = FontWeight.SemiBold)
                    }
                
                SormsButton(
                    onClick = onOrderService,
                    text = "Đặt dịch vụ",
                    variant = ButtonVariant.Secondary,
                        isOutlined = true,
                    modifier = Modifier.weight(1f)
                )
                }
            }
        }
    }
}

@Composable
private fun ActiveBookingsSection(
    activeBookings: List<Booking>,
    onViewQR: (Booking) -> Unit,
    onNavigateToHistory: () -> Unit
) {
    SormsCard {
        Column(
            modifier = Modifier.padding(DesignSystem.Spacing.cardPadding)
        ) {
            // Header - đồng bộ với web
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (activeBookings.isNotEmpty()) "Phòng hiện tại" else "Chưa có phòng đặt",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (activeBookings.isNotEmpty())
                            "Các phòng bạn đang ở hoặc đã được duyệt"
                        else 
                            "Bạn chưa có phòng nào đang được đặt",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                if (activeBookings.isNotEmpty()) {
                    TextButton(onClick = onNavigateToHistory) {
                        Text(
                            text = "Xem tất cả",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(DesignSystem.Spacing.md))
            
            // Bookings List - đồng bộ với web: hiển thị tối đa 2 bookings
            if (activeBookings.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Bạn chưa có phòng nào đang được đặt",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.md)
                ) {
                    activeBookings.take(2).forEach { booking ->
                        ActiveBookingCard(
                            booking = booking,
                            onViewQR = { onViewQR(booking) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActiveBookingCard(
    booking: Booking,
    onViewQR: () -> Unit
) {
    val daysRemaining = if (booking.status.equals("CHECKED_IN", ignoreCase = true)) {
        getDaysRemaining(booking.checkOutDate)
    } else null
    
    val showCheckout = booking.status.equals("CHECKED_IN", ignoreCase = true)
    val canViewQR = (booking.status.equals("APPROVED", ignoreCase = true) || 
                     booking.status.equals("CHECKED_IN", ignoreCase = true)) && 
                    !booking.qrImageUrl.isNullOrBlank()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(DesignSystem.Spacing.cardContentPadding)
        ) {
            // Room code + Status + Dates - đồng bộ với web layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                        Text(
                            text = booking.code,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        SormsBadge(
                            text = StatusUtils.getBookingStatusText(booking.status),
                            tone = StatusUtils.getBookingStatusBadgeTone(booking.status)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${DateUtils.formatDateShort(booking.checkInDate)} - ${DateUtils.formatDateShort(booking.checkOutDate)}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            
            // "Còn X ngày" + Action buttons - đồng bộ với web
            Spacer(modifier = Modifier.height(DesignSystem.Spacing.sm))
            Row(
                    modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
                ) {
                // "Còn X ngày" display
                if (daysRemaining != null && daysRemaining > 0) {
                    Text(
                        text = "Còn ",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "$daysRemaining ngày",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
                
                // Action buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (canViewQR) {
                        OutlinedButton(
                            onClick = onViewQR,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Xem QR", fontSize = 14.sp)
                        }
                    }
                    if (showCheckout) {
                        Button(
                            onClick = { /* TODO: Navigate to checkout */ }
                        ) {
                            Text("Check-out", fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookingInfoCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun QuickActionsSection(
    onNavigateToRooms: () -> Unit,
    onNavigateToServices: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val actions = listOf(
        QuickAction("Đặt phòng", Icons.Default.Home, onNavigateToRooms),
        QuickAction("Dịch vụ", Icons.Default.CleaningServices, onNavigateToServices),
        QuickAction("Hóa đơn", Icons.Default.Receipt, onNavigateToOrders),
        QuickAction("Lịch sử", Icons.Default.History, onNavigateToHistory)
    )
    
    SormsCard {
        Column(
            modifier = Modifier.padding(DesignSystem.Spacing.cardPadding)  // Sử dụng DesignSystem spacing
        ) {
            Text(
                text = "Thao tác nhanh",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(DesignSystem.Spacing.md))  // Sử dụng DesignSystem spacing
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.elementSpacing)  // Tăng spacing từ 12dp
            ) {
                actions.forEach { action ->
                    QuickActionItem(
                        action = action,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionItem(
    action: QuickAction,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = action.onClick,
        modifier = modifier
            .shadow(
                elevation = 2.dp,  // Giảm elevation từ 4dp
                shape = RoundedCornerShape(16.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)  // Giảm alpha
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),  // Giảm alpha từ 0.2f
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(DesignSystem.Spacing.cardContentPadding),  // Sử dụng DesignSystem spacing
            contentAlignment = Alignment.Center
        ) {
            Column(
            horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)  // Giảm từ 56dp để tối giản
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),  // Giảm alpha từ 0.15f
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.title,
                        modifier = Modifier.size(24.dp),  // Giảm từ 28dp
                tint = MaterialTheme.colorScheme.primary
            )
                }
            
                Spacer(modifier = Modifier.height(DesignSystem.Spacing.sm))  // Sử dụng DesignSystem spacing
            
            Text(
                text = action.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            }
        }
    }
}

// Helper function để tính số ngày còn lại (đồng bộ với web)
private fun getDaysRemaining(checkOutDate: Date): Int {
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val checkout = Calendar.getInstance().apply {
        time = checkOutDate
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val diff = checkout.timeInMillis - today.timeInMillis
    return maxOf(0, (diff / (1000 * 60 * 60 * 24)).toInt())
}

@Composable
private fun PendingBookingsSection(
    pendingBookings: List<Booking>,
    onNavigateToHistory: () -> Unit
) {
    // Đồng bộ với web: Card với màu cam/orange
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(DesignSystem.Spacing.cardPadding)
        ) {
            // Header - đồng bộ với web
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Đang chờ duyệt",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${pendingBookings.size} yêu cầu đang chờ xử lý",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(DesignSystem.Spacing.md))
            
            // Pending bookings list (tối đa 2 items, giống web) - đồng bộ với web: màu cam
            Column(
                verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.md)
            ) {
                pendingBookings.take(2).forEach { booking ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Yellow500.copy(alpha = 0.1f)  // Đồng bộ với web: màu cam nhẹ
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Yellow500.copy(alpha = 0.3f)  // Border màu cam
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(DesignSystem.Spacing.cardContentPadding)  // Sử dụng DesignSystem spacing
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = booking.code,  // Đồng bộ với web: hiển thị booking code
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${DateUtils.formatDateShort(booking.checkInDate)} - ${DateUtils.formatDateShort(booking.checkOutDate)}",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                                SormsBadge(
                                    text = "Chờ duyệt",
                                    tone = BadgeTone.Warning
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(DesignSystem.Spacing.sm))  // Sử dụng DesignSystem spacing
                            
                            // Action buttons (giống web: Chi tiết, Điều chỉnh, Hủy)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.elementSpacing)  // Tăng spacing
                            ) {
                                OutlinedButton(
                                    onClick = { /* Navigate to detail */ },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Chi tiết", fontSize = 12.sp)
                                }
                                OutlinedButton(
                                    onClick = { /* Navigate to edit */ },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = Yellow500.copy(alpha = 0.1f),  // Đồng bộ với web: màu cam
                                        contentColor = Yellow800
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        Yellow500.copy(alpha = 0.3f)
                                    )
                                ) {
                                    Text("Điều chỉnh", fontSize = 12.sp)
                                }
                                Button(
                                    onClick = { /* Cancel booking */ },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer,
                                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                ) {
                                    Text("Hủy", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummarySection(
    activeBookings: Int,
    pendingBookings: Int,  // Đồng bộ với web: thêm pending count
    serviceOrders: Int,
    unpaidOrders: Int,
    faceRegistrationStatus: Boolean?,  // Đồng bộ với web: thêm face status
    onFaceRegisterClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,  // Giảm elevation từ 4dp
                shape = RoundedCornerShape(20.dp),
                spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)  // Giảm alpha
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(DesignSystem.Spacing.cardPadding)  // Sử dụng DesignSystem spacing
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                Text(
                    text = "Tóm tắt",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                }
                
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            color = Green500,
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                )
            }
            
            Spacer(modifier = Modifier.height(DesignSystem.Spacing.lg))  // Sử dụng DesignSystem spacing
            
            Column(
                verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.md)  // Tăng spacing từ 16dp
            ) {
                SummaryItem(
                    label = "Phòng đang ở",
                    value = activeBookings.toString(),
                    icon = Icons.Default.Home,
                    color = MaterialTheme.colorScheme.primary
                )
                SummaryItem(
                    label = "Chờ duyệt",  // Đồng bộ với web: thêm pending count
                    value = pendingBookings.toString(),
                    icon = Icons.Default.HourglassTop,
                    color = Yellow500
                )
                SummaryItem(
                    label = "Đơn dịch vụ",
                    value = serviceOrders.toString(),
                    icon = Icons.Default.CleaningServices,
                    color = Green500
                )
                // Face registration status (đồng bộ với web)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onFaceRegisterClick),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)  // Giảm từ 40dp để tối giản
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),  // Giảm alpha từ 0.15f
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Face,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),  // Giảm từ 20dp
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = "Xác thực khuôn mặt",
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Text(
                        text = when (faceRegistrationStatus) {
                            true -> "Đã đăng ký"
                            false -> "Chưa"
                            null -> "-"
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (faceRegistrationStatus == true) Green500 else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                thickness = 1.dp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SormsButton(
                onClick = { /* Navigate to history */ },
                text = "Xem lịch sử thuê",
                variant = ButtonVariant.Secondary,
                isOutlined = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SummaryItem(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = color.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = color
                )
            }
            
        Text(
            text = label,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
        )
        }
        
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

// Helper data classes and functions
private data class QuickAction(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Preview(showBackground = true, name = "User Dashboard Screen", device = "spec:width=411dp,height=891dp")
@Composable
private fun UserDashboardScreenPreview() {
    com.example.sorms_app.presentation.theme.SORMS_appTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            // Preview với mock data
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(DesignSystem.Spacing.screenHorizontal),
                    verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.md)
                ) {
                    item {
                        WelcomeSection(
                            userName = "Nguyễn Văn A",
                            onBookRoom = {},
                            onOrderService = {}
                        )
                    }
                    
                    item {
                        ActiveBookingsSection(
                            activeBookings = emptyList(),
                            onNavigateToHistory = {},
                            onViewQR = {}
                        )
                    }
                    
                    item {
                        QuickActionsSection(
                            onNavigateToRooms = {},
                            onNavigateToServices = {},
                            onNavigateToOrders = {},
                            onNavigateToHistory = {}
                        )
                    }
                    
                    item {
                        SummarySection(
                            activeBookings = 2,
                            serviceOrders = 5,
                            unpaidOrders = 1,
                            pendingBookings = 0,
                            faceRegistrationStatus = null,
                            onFaceRegisterClick = {}
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "User Dashboard - With Booking", device = "spec:width=411dp,height=891dp")
@Composable
private fun UserDashboardScreenWithBookingPreview() {
    com.example.sorms_app.presentation.theme.SORMS_appTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val mockBooking = Booking(
                id = 1,
                code = "BK-2024-001",
                roomName = "Phòng 101",
                roomId = 101,
                buildingName = "Tòa A",
                checkInDate = java.util.Date(),
                checkOutDate = java.util.Date(System.currentTimeMillis() + 86400000 * 3),
                status = "APPROVED",
                numberOfGuests = 2,
                notes = "Yêu cầu phòng yên tĩnh"
            )
            
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(DesignSystem.Spacing.screenHorizontal),
                    verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.md)
                ) {
                    item {
                        WelcomeSection(
                            userName = "Nguyễn Văn A",
                            onBookRoom = {},
                            onOrderService = {}
                        )
                    }
                    
                    item {
                        ActiveBookingsSection(
                            activeBookings = listOf(mockBooking),
                            onNavigateToHistory = {},
                            onViewQR = {}
                        )
                    }
                    
                    item {
                        QuickActionsSection(
            onNavigateToRooms = {},
            onNavigateToServices = {},
            onNavigateToOrders = {},
                            onNavigateToHistory = {}
                        )
                    }
                    
                    item {
                        SummarySection(
                            activeBookings = 2,
                            serviceOrders = 5,
                            unpaidOrders = 1,
                            pendingBookings = 0,
                            faceRegistrationStatus = null,
                            onFaceRegisterClick = {}
                        )
                    }
                }
            }
        }
    }
}
