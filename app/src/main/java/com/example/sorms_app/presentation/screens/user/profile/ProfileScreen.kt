package com.example.sorms_app.presentation.screens.user.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sorms_app.presentation.components.*
import com.example.sorms_app.presentation.theme.DesignSystem
import com.example.sorms_app.presentation.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onNavigateToFaceManagement: () -> Unit,
    userViewModel: UserViewModel = hiltViewModel()
) {
    val uiState by userViewModel.uiState.collectAsState()
    var showConfirmLogout by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        SormsTopAppBar(
            title = "Tài khoản"
        )

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(DesignSystem.Spacing.screenHorizontal),
                    verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.md)
                ) {
                    // Profile Info Card
                    SormsCard {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Avatar
                            Card(
                                modifier = Modifier.size(80.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                shape = CircleShape
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Avatar",
                                        modifier = Modifier.size(40.dp),
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                            
                            if (uiState.user != null) {
                                Text(
                                    text = uiState.user!!.name,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Text(
                                    text = uiState.user!!.email,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            } else {
                                Text(
                                    text = "Người dùng",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            SormsBadge(
                                text = "Người dùng",
                                tone = BadgeTone.Success
                            )
                        }
                    }

                    // Menu Items
                    SormsCard {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Cài đặt",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Face Management
                            MenuItemRow(
                                icon = Icons.Default.Face,
                                title = "Quản lý nhận diện",
                                subtitle = "Đăng ký khuôn mặt và CCCD",
                                onClick = onNavigateToFaceManagement
                            )
                            
                            MenuItemRow(
                                icon = Icons.Default.Person,
                                title = "Thông tin cá nhân",
                                subtitle = "Cập nhật thông tin của bạn",
                                onClick = { /* TODO: Navigate to edit profile */ }
                            )
                            
                            MenuItemRow(
                                icon = Icons.Default.History,
                                title = "Lịch sử đặt phòng",
                                subtitle = "Xem các lần đặt phòng trước",
                                onClick = { /* TODO: Navigate to history */ }
                            )
                            
                            MenuItemRow(
                                icon = Icons.Default.Notifications,
                                title = "Thông báo",
                                subtitle = "Cài đặt thông báo",
                                onClick = { /* TODO: Navigate to notifications */ }
                            )
                            
                            MenuItemRow(
                                icon = Icons.Default.Settings,
                                title = "Cài đặt",
                                subtitle = "Tùy chỉnh ứng dụng",
                                onClick = { /* TODO: Navigate to settings */ }
                            )
                        }
                    }

                    // Logout Section
                    SormsCard {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            SormsButton(
                                onClick = { showConfirmLogout = true },
                                text = "Đăng xuất",
                                variant = ButtonVariant.Danger,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    
                    // App Version
                    Text(
                        text = "Phiên bản 1.0.0",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }

    // Logout Confirmation Dialog
    if (showConfirmLogout) {
        AlertDialog(
            onDismissRequest = { showConfirmLogout = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Xác nhận đăng xuất") },
            text = { Text("Bạn có chắc chắn muốn đăng xuất khỏi tài khoản?") },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmLogout = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) { 
                    Text("Đăng xuất") 
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmLogout = false }) { 
                    Text("Hủy") 
                }
            }
        )
    }
}

@Composable
private fun MenuItemRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Mở",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}
