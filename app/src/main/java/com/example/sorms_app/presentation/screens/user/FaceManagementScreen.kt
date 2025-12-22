package com.example.sorms_app.presentation.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sorms_app.presentation.components.*
import com.example.sorms_app.presentation.viewmodel.FaceManagementViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaceManagementScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FaceManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadFaceData()
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { 
                Text(
                    text = "Quản lý nhận diện",
                    fontWeight = FontWeight.SemiBold
                ) 
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Quay lại"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
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
            
            uiState.errorMessage != null -> {
                SormsEmptyState(
                    title = "Có lỗi xảy ra",
                    subtitle = uiState.errorMessage!!
                )
            }
            
            !uiState.hasFaceData -> {
                // No face data registered
                NoFaceDataState(
                    onRegister = onNavigateToRegister
                )
            }
            
            else -> {
                // Has face data
                FaceDataManagement(
                    uiState = uiState,
                    onRegisterNew = onNavigateToRegister,
                    onDeleteFaceData = { showDeleteDialog = true },
                    onRefresh = { viewModel.loadFaceData() }
                )
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xóa dữ liệu nhận diện") },
            text = { 
                Text("Bạn có chắc chắn muốn xóa tất cả dữ liệu nhận diện? Hành động này không thể hoàn tác và bạn sẽ không thể booking cho đến khi đăng ký lại.") 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteFaceData()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Xóa", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}

@Composable
private fun NoFaceDataState(
    onRegister: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Face Icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FaceRetouchingOff,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = "Chưa đăng ký nhận diện",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        SormsCard {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Để có thể đặt phòng, bạn cần:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                RequirementItem(
                    icon = Icons.Default.Face,
                    text = "Đăng ký khuôn mặt (3 góc độ)"
                )

                RequirementItem(
                    icon = Icons.Default.CreditCard,
                    text = "Xác thực CCCD (2 mặt)"
                )

                RequirementItem(
                    icon = Icons.Default.Security,
                    text = "Dữ liệu được mã hóa an toàn"
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        SormsButton(
            onClick = onRegister,
            text = "Đăng ký nhận diện",
            variant = ButtonVariant.Primary,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun FaceDataManagement(
    uiState: FaceManagementUiState,
    onRegisterNew: () -> Unit,
    onDeleteFaceData: () -> Unit,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status Card
        SormsCard {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Nhận diện đã kích hoạt",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                SormsBadge(
                    text = "Đã xác thực",
                    tone = BadgeTone.Success
                )
            }
        }

        // Registration Info
        SormsCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Thông tin đăng ký",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                InfoRow(
                    icon = Icons.Default.DateRange,
                    label = "Ngày đăng ký",
                    value = formatDate(uiState.registrationDate)
                )

                InfoRow(
                    icon = Icons.Default.Update,
                    label = "Cập nhật lần cuối",
                    value = formatDate(uiState.lastUpdateDate)
                )

                InfoRow(
                    icon = Icons.Default.PhotoCamera,
                    label = "Số ảnh đã lưu",
                    value = "${uiState.imageCount} ảnh"
                )

                InfoRow(
                    icon = Icons.Default.CreditCard,
                    label = "Trạng thái CCCD",
                    value = if (uiState.isIdVerified) "Đã xác thực" else "Chưa xác thực"
                )
            }
        }

        // Features Card
        SormsCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Tính năng đã kích hoạt",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                FeatureItem(
                    icon = Icons.Default.Speed,
                    title = "Check-in nhanh",
                    description = "Tự động nhận diện khi vào khách sạn",
                    isEnabled = true
                )

                FeatureItem(
                    icon = Icons.Default.Hotel,
                    title = "Đặt phòng",
                    description = "Có thể đặt phòng trực tuyến",
                    isEnabled = uiState.canBookRoom
                )

                FeatureItem(
                    icon = Icons.Default.Security,
                    title = "Bảo mật nâng cao",
                    description = "Xác thực 2 lớp với khuôn mặt",
                    isEnabled = true
                )
            }
        }

        // Actions Card
        SormsCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Quản lý dữ liệu",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onRefresh,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Làm mới")
                    }

                    SormsButton(
                        onClick = onRegisterNew,
                        text = "Đăng ký lại",
                        variant = ButtonVariant.Secondary,
                        modifier = Modifier.weight(1f)
                    )
                }

                SormsButton(
                    onClick = onDeleteFaceData,
                    text = "Xóa dữ liệu nhận diện",
                    variant = ButtonVariant.Danger,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Warning Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
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
                        text = "Lưu ý quan trọng",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error
                    )

                    Text(
                        text = "Nếu xóa dữ liệu nhận diện, bạn sẽ không thể đặt phòng cho đến khi đăng ký lại đầy đủ.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun RequirementItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = text,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = label,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun FeatureItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    isEnabled: Boolean
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )

            Text(
                text = description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        Icon(
            imageVector = if (isEnabled) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

private fun formatDate(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return "N/A"
    return try {
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(dateString)
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formatter.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}

// Data class for UI state
data class FaceManagementUiState(
    val isLoading: Boolean = false,
    val hasFaceData: Boolean = false,
    val registrationDate: String? = null,
    val lastUpdateDate: String? = null,
    val imageCount: Int = 0,
    val isIdVerified: Boolean = false,
    val canBookRoom: Boolean = false,
    val errorMessage: String? = null,
    val isDeleting: Boolean = false
)