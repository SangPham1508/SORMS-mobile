package com.example.sorms_app.presentation.screens.user

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sorms_app.data.models.RoomData
import com.example.sorms_app.presentation.components.*
import com.example.sorms_app.presentation.theme.DesignSystem
import com.example.sorms_app.presentation.viewmodel.BookingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDetailScreen(
    room: RoomData,
    onNavigateBack: () -> Unit,
    onBookingSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedDate by remember { mutableStateOf("") }
    var checkInTime by remember { mutableStateOf("14:00") }
    var checkOutTime by remember { mutableStateOf("12:00") }
    var guestCount by remember { mutableStateOf(1) }
    var specialRequests by remember { mutableStateOf("") }

    LaunchedEffect(uiState.bookingSuccess) {
        if (uiState.bookingSuccess) {
            onBookingSuccess()
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Top App Bar
        SormsTopAppBar(
            title = "Đặt phòng",
            onNavigateBack = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(DesignSystem.Spacing.screenHorizontal),
            verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.md)
        ) {
            // Room Info Card
            SormsCard {
                Column(
                    modifier = Modifier.padding(DesignSystem.Spacing.cardContentPadding)
                ) {
                    Text(
                        text = room.name ?: room.code ?: "Phòng #${room.id}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        InfoChip(
                            icon = Icons.Default.Business,
                            text = "Tầng ${room.floor ?: "N/A"}"
                        )
                        InfoChip(
                            icon = Icons.Default.People,
                            text = "${room.capacity ?: "N/A"} người"
                        )
                    }
                    
                    room.description?.let { description ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = description,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Booking Form
            SormsCard {
                Column(
                    modifier = Modifier.padding(DesignSystem.Spacing.cardContentPadding),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Thông tin đặt phòng",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    // Date Selection
                    OutlinedTextField(
                        value = selectedDate,
                        onValueChange = { selectedDate = it },
                        label = { Text("Ngày đặt phòng") },
                        placeholder = { Text("dd/MM/yyyy") },
                        leadingIcon = {
                            Icon(Icons.Default.DateRange, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Time Selection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = checkInTime,
                            onValueChange = { checkInTime = it },
                            label = { Text("Giờ nhận phòng") },
                            placeholder = { Text("14:00") },
                            leadingIcon = {
                                Icon(Icons.Default.Schedule, contentDescription = null)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        
                        OutlinedTextField(
                            value = checkOutTime,
                            onValueChange = { checkOutTime = it },
                            label = { Text("Giờ trả phòng") },
                            placeholder = { Text("12:00") },
                            leadingIcon = {
                                Icon(Icons.Default.Schedule, contentDescription = null)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Guest Count
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Số lượng khách",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = { if (guestCount > 1) guestCount-- },
                                enabled = guestCount > 1
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Giảm")
                            }
                            
                            Text(
                                text = guestCount.toString(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            
                            IconButton(
                                onClick = { 
                                    val maxCapacity = room.capacity?.toIntOrNull() ?: 4
                                    if (guestCount < maxCapacity) guestCount++
                                },
                                enabled = guestCount < (room.capacity?.toIntOrNull() ?: 4)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Tăng")
                            }
                        }
                    }

                    // Special Requests
                    OutlinedTextField(
                        value = specialRequests,
                        onValueChange = { specialRequests = it },
                        label = { Text("Yêu cầu đặc biệt") },
                        placeholder = { Text("Nhập yêu cầu đặc biệt (tùy chọn)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5
                    )
                }
            }

            // Pricing Summary
            SormsCard {
                Column(
                    modifier = Modifier.padding(DesignSystem.Spacing.cardContentPadding),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Tóm tắt giá",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Giá phòng/đêm")
                        Text("Liên hệ", fontWeight = FontWeight.Medium)
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Phí dịch vụ")
                        Text("Miễn phí", fontWeight = FontWeight.Medium)
                    }
                    
                    Divider()
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Tổng cộng",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Liên hệ",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Book Button
            if (uiState.requiresFaceRegistration) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(DesignSystem.Spacing.cardContentPadding),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.error
                            )

                            Column {
                                Text(
                                    text = "Yêu cầu đăng ký nhận diện",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.error
                                )

                                Text(
                                    text = "Để đảm bảo an toàn, bạn cần hoàn thành đăng ký nhận diện khuôn mặt và xác thực CCCD trước khi đặt phòng.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Quay lại")
                        }

                        SormsButton(
                            onClick = { /* Navigate to face registration */ },
                            text = "Đăng ký nhận diện",
                            variant = ButtonVariant.Primary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            } else {
                SormsButton(
                    onClick = {
                        viewModel.createBooking(
                            roomId = room.id,
                            checkInDate = selectedDate,
                            checkInTime = checkInTime,
                            checkOutTime = checkOutTime,
                            guestCount = guestCount,
                            specialRequests = specialRequests
                        )
                    },
                    text = if (uiState.isLoading) "Đang xử lý..." else "Xác nhận đặt phòng",
                    variant = ButtonVariant.Primary,
                    enabled = !uiState.isLoading && selectedDate.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Error Message
            uiState.errorMessage?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(DesignSystem.Spacing.cardContentPadding)
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = text,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}