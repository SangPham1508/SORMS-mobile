package com.example.sorms_app.presentation.screens.user

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sorms_app.presentation.components.*
import com.example.sorms_app.presentation.viewmodel.FaceRegisterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaceRegisterScreen(
    onNavigateBack: () -> Unit,
    onRegistrationSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FaceRegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.registrationSuccess) {
        if (uiState.registrationSuccess) {
            onRegistrationSuccess()
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { 
                Text(
                    text = "Đăng ký nhận diện",
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Progress Indicator
            val totalSteps = FaceRegisterStep.values().size
            val currentStepIndex = FaceRegisterStep.values().indexOf(uiState.currentStep)
            
            LinearProgressIndicator(
                progress = (currentStepIndex + 1) / totalSteps.toFloat(),
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )

            when (uiState.currentStep) {
                FaceRegisterStep.INTRODUCTION -> IntroductionStep(
                    onNext = { viewModel.nextStep() }
                )
                
                FaceRegisterStep.CAMERA_PERMISSION -> CameraPermissionStep(
                    onNext = { viewModel.nextStep() },
                    onBack = { viewModel.previousStep() }
                )
                
                FaceRegisterStep.FACE_LEFT -> CaptureStep(
                    captureType = CaptureType.FACE_LEFT,
                    uiState = uiState,
                    onCapture = { viewModel.captureImage(CaptureType.FACE_LEFT) },
                    onRetry = { viewModel.retryCapture() },
                    onNext = { viewModel.nextStep() },
                    onBack = { viewModel.previousStep() }
                )
                
                FaceRegisterStep.FACE_RIGHT -> CaptureStep(
                    captureType = CaptureType.FACE_RIGHT,
                    uiState = uiState,
                    onCapture = { viewModel.captureImage(CaptureType.FACE_RIGHT) },
                    onRetry = { viewModel.retryCapture() },
                    onNext = { viewModel.nextStep() },
                    onBack = { viewModel.previousStep() }
                )
                
                FaceRegisterStep.FACE_FRONT -> CaptureStep(
                    captureType = CaptureType.FACE_FRONT,
                    uiState = uiState,
                    onCapture = { viewModel.captureImage(CaptureType.FACE_FRONT) },
                    onRetry = { viewModel.retryCapture() },
                    onNext = { viewModel.nextStep() },
                    onBack = { viewModel.previousStep() }
                )
                
                FaceRegisterStep.ID_CARD_FRONT -> CaptureStep(
                    captureType = CaptureType.ID_CARD_FRONT,
                    uiState = uiState,
                    onCapture = { viewModel.captureImage(CaptureType.ID_CARD_FRONT) },
                    onRetry = { viewModel.retryCapture() },
                    onNext = { viewModel.nextStep() },
                    onBack = { viewModel.previousStep() }
                )
                
                FaceRegisterStep.ID_CARD_BACK -> CaptureStep(
                    captureType = CaptureType.ID_CARD_BACK,
                    uiState = uiState,
                    onCapture = { viewModel.captureImage(CaptureType.ID_CARD_BACK) },
                    onRetry = { viewModel.retryCapture() },
                    onNext = { viewModel.nextStep() },
                    onBack = { viewModel.previousStep() }
                )
                
                FaceRegisterStep.REVIEW -> ReviewStep(
                    uiState = uiState,
                    onConfirm = { viewModel.nextStep() },
                    onBack = { viewModel.previousStep() },
                    onRetakeImage = { captureType -> viewModel.retakeImage(captureType) }
                )
                
                FaceRegisterStep.PROCESSING -> ProcessingStep(
                    uiState = uiState,
                    onComplete = { viewModel.completeFaceRegistration() }
                )
            }
        }
    }
}

@Composable
private fun IntroductionStep(
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Face Icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Face,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = "Đăng ký nhận diện khuôn mặt",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        SormsCard {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Lợi ích của việc đăng ký khuôn mặt:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                BenefitItem(
                    icon = Icons.Default.Speed,
                    title = "Check-in nhanh chóng",
                    description = "Không cần quét QR code, chỉ cần nhìn vào camera"
                )

                BenefitItem(
                    icon = Icons.Default.Security,
                    title = "Bảo mật cao",
                    description = "Dữ liệu được mã hóa và bảo vệ an toàn"
                )

                BenefitItem(
                    icon = Icons.Default.VerifiedUser,
                    title = "Xác thực danh tính",
                    description = "Yêu cầu CCCD để đảm bảo an toàn"
                )

                BenefitItem(
                    icon = Icons.Default.PersonalVideo,
                    title = "Trải nghiệm cá nhân hóa",
                    description = "Hệ thống tự động nhận diện và chào đón"
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        SormsButton(
            onClick = onNext,
            text = "Bắt đầu đăng ký",
            variant = ButtonVariant.Primary,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun CameraPermissionStep(
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Camera Icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = "Cấp quyền truy cập camera",
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
                    text = "Để đăng ký nhận diện, chúng tôi cần:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "• Quyền truy cập camera để chụp ảnh",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                Text(
                    text = "• 3 ảnh khuôn mặt (trái, phải, chính diện)",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                Text(
                    text = "• Ảnh CCCD 2 mặt để xác thực danh tính",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                Text(
                    text = "• Dữ liệu được xử lý cục bộ và mã hóa",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("Quay lại")
            }

            SormsButton(
                onClick = onNext,
                text = "Cấp quyền",
                variant = ButtonVariant.Primary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CaptureStep(
    captureType: CaptureType,
    uiState: FaceRegisterUiState,
    onCapture: () -> Unit,
    onRetry: () -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val isCaptured = uiState.capturedImages.containsKey(captureType)
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = captureType.displayName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        // Camera Preview Placeholder
        Card(
            modifier = Modifier
                .size(280.dp)
                .aspectRatio(1f),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    uiState.isCapturing -> {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    isCaptured -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Chụp thành công!",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    else -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (captureType.name.startsWith("ID_CARD")) 
                                    Icons.Default.CreditCard else Icons.Default.Face,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "Camera Preview",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }

        // Instructions
        SormsCard {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Hướng dẫn:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "• ${captureType.instruction}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                when (captureType) {
                    CaptureType.FACE_LEFT, CaptureType.FACE_RIGHT, CaptureType.FACE_FRONT -> {
                        Text(
                            text = "• Đảm bảo ánh sáng đủ và không bị che khuất",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "• Giữ yên trong 2-3 giây",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                    CaptureType.ID_CARD_FRONT, CaptureType.ID_CARD_BACK -> {
                        Text(
                            text = "• Đặt CCCD trên nền phẳng, ánh sáng đều",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "• Chụp rõ toàn bộ thẻ, không bị cắt góc",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }
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
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f),
                enabled = !uiState.isCapturing
            ) {
                Text("Quay lại")
            }

            when {
                isCaptured -> {
                    SormsButton(
                        onClick = onNext,
                        text = "Tiếp tục",
                        variant = ButtonVariant.Primary,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                uiState.errorMessage != null -> {
                    SormsButton(
                        onClick = onRetry,
                        text = "Thử lại",
                        variant = ButtonVariant.Primary,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                else -> {
                    SormsButton(
                        onClick = onCapture,
                        text = if (uiState.isCapturing) "Đang chụp..." else "Chụp ảnh",
                        variant = ButtonVariant.Primary,
                        enabled = !uiState.isCapturing,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewStep(
    uiState: FaceRegisterUiState,
    onConfirm: () -> Unit,
    onBack: () -> Unit,
    onRetakeImage: (CaptureType) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Xem lại ảnh đã chụp",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(CaptureType.values()) { captureType ->
                ReviewImageCard(
                    captureType = captureType,
                    isCapture = uiState.capturedImages.containsKey(captureType),
                    onRetake = { onRetakeImage(captureType) }
                )
            }
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("Quay lại")
            }

            SormsButton(
                onClick = onConfirm,
                text = "Xác nhận đăng ký",
                variant = ButtonVariant.Primary,
                enabled = uiState.capturedImages.size == CaptureType.values().size,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ReviewImageCard(
    captureType: CaptureType,
    isCapture: Boolean,
    onRetake: () -> Unit
) {
    SormsCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = if (isCapture) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = if (isCapture) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = captureType.displayName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = if (isCapture) "Đã chụp" else "Chưa chụp",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            if (isCapture) {
                TextButton(onClick = onRetake) {
                    Text("Chụp lại")
                }
            }
        }
    }
}

@Composable
private fun ProcessingStep(
    uiState: FaceRegisterUiState,
    onComplete: () -> Unit
) {
    LaunchedEffect(Unit) {
        onComplete()
    }
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Đang xử lý dữ liệu",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        CircularProgressIndicator(
            modifier = Modifier.size(64.dp),
            color = MaterialTheme.colorScheme.primary
        )

        SormsCard {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Đang xử lý và mã hóa dữ liệu...",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = "Quá trình này có thể mất vài giây",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun BenefitItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

// Data classes for UI state
data class FaceRegisterUiState(
    val isCapturing: Boolean = false,
    val currentStep: FaceRegisterStep = FaceRegisterStep.INTRODUCTION,
    val capturedImages: Map<CaptureType, String> = emptyMap(),
    val isRegistering: Boolean = false,
    val registrationSuccess: Boolean = false,
    val errorMessage: String? = null,
    val canProceedToNext: Boolean = false
)

enum class FaceRegisterStep {
    INTRODUCTION,
    CAMERA_PERMISSION,
    FACE_LEFT,
    FACE_RIGHT,
    FACE_FRONT,
    ID_CARD_FRONT,
    ID_CARD_BACK,
    REVIEW,
    PROCESSING
}

enum class CaptureType(val displayName: String, val instruction: String) {
    FACE_LEFT("Khuôn mặt trái", "Xoay mặt sang trái 45 độ"),
    FACE_RIGHT("Khuôn mặt phải", "Xoay mặt sang phải 45 độ"),
    FACE_FRONT("Khuôn mặt chính diện", "Nhìn thẳng vào camera"),
    ID_CARD_FRONT("CCCD mặt trước", "Chụp rõ mặt trước CCCD"),
    ID_CARD_BACK("CCCD mặt sau", "Chụp rõ mặt sau CCCD")
}