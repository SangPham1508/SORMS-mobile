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
import com.example.sorms_app.presentation.theme.DesignSystem
import com.example.sorms_app.presentation.theme.Green500
import com.example.sorms_app.presentation.theme.SORMS_appTheme
import com.example.sorms_app.presentation.theme.Yellow500
import com.example.sorms_app.presentation.utils.DateUtils
import com.example.sorms_app.presentation.utils.StatusUtils
import com.example.sorms_app.presentation.viewmodel.UserDashboardViewModel

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
        verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.md)
    ) {
        // Welcome Section
        item {
            WelcomeSection(
                userName = uiState.userName,
                onBookRoom = onNavigateToRooms,
                onOrderService = onNavigateToServices
            )
        }

        // Current Booking Section
        item {
            CurrentBookingSection(
                currentBooking = uiState.currentBooking,
                onBookRoom = onNavigateToRooms,
                onOrderService = onNavigateToServices,
                onViewOrders = onNavigateToOrders,
                onFaceRegister = onNavigateToFaceRegister
            )
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
                serviceOrders = uiState.serviceOrdersCount,
                unpaidOrders = uiState.unpaidOrdersCount
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
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
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
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
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
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
            )
            
            Text(
                text = userName,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 6.dp)
            )
            
            Text(
                text = "Quản lý nhanh đặt phòng, dịch vụ và hóa đơn của bạn.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                lineHeight = 20.sp
            )
                    }
                    
                    // Decorative icon
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                }
            
                Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
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
private fun CurrentBookingSection(
    currentBooking: Booking?,
    onBookRoom: () -> Unit,
    onOrderService: () -> Unit,
    onViewOrders: () -> Unit,
    onFaceRegister: () -> Unit
) {
    SormsCard {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (currentBooking != null) "Phòng hiện tại" else "Chưa có phòng đặt",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = if (currentBooking != null) 
                            "Thông tin đặt phòng đang diễn ra" 
                        else 
                            "Bạn chưa có phòng nào đang được đặt",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                currentBooking?.let { booking ->
                    SormsBadge(
                        text = StatusUtils.getBookingStatusText(booking.status),
                        tone = StatusUtils.getBookingStatusBadgeTone(booking.status)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (currentBooking != null) {
                // Booking Details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BookingInfoCard(
                        title = "Phòng",
                        value = currentBooking.roomName,
                        modifier = Modifier.weight(1f)
                    )
                    
                    BookingInfoCard(
                        title = "Check-in",
                        value = DateUtils.formatDateShort(currentBooking.checkInDate),
                        modifier = Modifier.weight(1f)
                    )
                    
                    BookingInfoCard(
                        title = "Check-out",
                        value = DateUtils.formatDateShort(currentBooking.checkOutDate),
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Action Buttons
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        OutlinedButton(
                            onClick = onBookRoom,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Đặt phòng mới")
                        }
                    }
                    
                    item {
                        SormsButton(
                            onClick = onOrderService,
                            text = "Đặt dịch vụ",
                            variant = ButtonVariant.Secondary,
                            isOutlined = true
                        )
                    }
                    
                    item {
                        SormsButton(
                            onClick = onViewOrders,
                            text = "Xem hóa đơn",
                            variant = ButtonVariant.Secondary,
                            isOutlined = true
                        )
                    }
                    
                    item {
                        SormsButton(
                            onClick = onFaceRegister,
                            text = "Đăng ký khuôn mặt",
                            variant = ButtonVariant.Secondary,
                            isOutlined = true
                        )
                    }
                }
            } else {
                // No booking state
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Bạn chưa có phòng nào đang được đặt",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    SormsButton(
                        onClick = onBookRoom,
                        text = "Đặt phòng ngay",
                        variant = ButtonVariant.Primary,
                        modifier = Modifier.fillMaxWidth()
                    )
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
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Thao tác nhanh",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
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
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(18.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
            horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.title,
                        modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.primary
            )
                }
            
                Spacer(modifier = Modifier.height(12.dp))
            
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

@Composable
private fun SummarySection(
    activeBookings: Int,
    serviceOrders: Int,
    unpaidOrders: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
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
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SummaryItem(
                    label = "Đặt phòng đang ở",
                    value = activeBookings.toString(),
                    icon = Icons.Default.Home,
                    color = MaterialTheme.colorScheme.primary
                )
                SummaryItem(
                    label = "Dịch vụ đã đặt",
                    value = serviceOrders.toString(),
                    icon = Icons.Default.CleaningServices,
                    color = Green500
                )
                SummaryItem(
                    label = "Hóa đơn chờ",
                    value = unpaidOrders.toString(),
                    icon = Icons.Default.Receipt,
                    color = Yellow500
                )
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
                        CurrentBookingSection(
                            currentBooking = null,
                            onBookRoom = {},
                            onOrderService = {},
                            onViewOrders = {},
                            onFaceRegister = {}
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
                            unpaidOrders = 1
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
                        CurrentBookingSection(
                            currentBooking = mockBooking,
                            onBookRoom = {},
                            onOrderService = {},
                            onViewOrders = {},
                            onFaceRegister = {}
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
                            unpaidOrders = 1
                        )
                    }
                }
            }
        }
    }
}
