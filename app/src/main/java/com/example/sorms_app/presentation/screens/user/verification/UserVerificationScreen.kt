package com.example.sorms_app.presentation.screens.user.verification

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.sorms_app.presentation.components.SormsButton
import com.example.sorms_app.presentation.components.SormsCard
import com.example.sorms_app.presentation.components.SormsLoading
import com.example.sorms_app.presentation.viewmodel.UserVerificationViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserVerificationScreen(
    onVerificationComplete: () -> Unit,
    onBack: () -> Unit,
    viewModel: UserVerificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var currentImageType by remember { mutableStateOf<ImageType?>(null) }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempImageUri != null) {
            when (currentImageType) {
                ImageType.ID_FRONT -> viewModel.updateIdCardFrontUri(tempImageUri)
                ImageType.ID_BACK -> viewModel.updateIdCardBackUri(tempImageUri)
                ImageType.FACE_FRONT -> viewModel.updateFaceFrontUri(tempImageUri)
                ImageType.FACE_LEFT -> viewModel.updateFaceLeftUri(tempImageUri)
                ImageType.FACE_RIGHT -> viewModel.updateFaceRightUri(tempImageUri)
                null -> {}
            }
        }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted && currentImageType != null) {
            val file = File(context.cacheDir, "temp_${currentImageType!!.name}_${System.currentTimeMillis()}.jpg")
            tempImageUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            cameraLauncher.launch(tempImageUri!!)
        } else {
            Toast.makeText(context, "Cần quyền camera để chụp ảnh", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchCamera(type: ImageType) {
        currentImageType = type
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    // Handle messages
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    // Check if verification is complete
    LaunchedEffect(uiState.isProfileSaved, uiState.isFaceRegistered) {
        if (uiState.isProfileSaved && uiState.isFaceRegistered) {
            Toast.makeText(context, "Xác thực hoàn tất! Đang chuyển đến đặt phòng...", Toast.LENGTH_SHORT).show()
            onVerificationComplete()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Xác thực thông tin")
                        Text(
                            text = "Hoàn tất để đặt phòng",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading && !uiState.isExistingDataLoaded) {
            SormsLoading(modifier = Modifier.padding(innerPadding))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Progress indicator
                VerificationProgress(
                    step1Complete = viewModel.isProfileComplete(),
                    step2Complete = uiState.idCardFrontUri != null && uiState.idCardBackUri != null,
                    step3Complete = uiState.faceFrontUri != null && uiState.faceLeftUri != null && uiState.faceRightUri != null
                )

                // Step 1: Personal Information
                SormsCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "1. Thông tin cá nhân",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(Modifier.height(16.dp))

                        OutlinedTextField(
                            value = uiState.fullName,
                            onValueChange = { viewModel.updateFullName(it) },
                            label = { Text("Họ và tên *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = uiState.phoneNumber,
                            onValueChange = { viewModel.updatePhoneNumber(it) },
                            label = { Text("Số điện thoại *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = uiState.dateOfBirth,
                                onValueChange = { viewModel.updateDateOfBirth(it) },
                                label = { Text("Ngày sinh") },
                                placeholder = { Text("dd/MM/yyyy") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            
                            OutlinedTextField(
                                value = uiState.gender,
                                onValueChange = { viewModel.updateGender(it) },
                                label = { Text("Giới tính") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = uiState.address,
                            onValueChange = { viewModel.updateAddress(it) },
                            label = { Text("Địa chỉ") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }
                }

                // Step 2: ID Card (CCCD)
                SormsCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Badge,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "2. Căn cước công dân (CCCD)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        OutlinedTextField(
                            value = uiState.idCardNumber,
                            onValueChange = { viewModel.updateIdCardNumber(it) },
                            label = { Text("Số CCCD *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = uiState.idCardIssueDate,
                                onValueChange = { viewModel.updateIdCardIssueDate(it) },
                                label = { Text("Ngày cấp") },
                                placeholder = { Text("dd/MM/yyyy") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            
                            OutlinedTextField(
                                value = uiState.idCardIssuePlace,
                                onValueChange = { viewModel.updateIdCardIssuePlace(it) },
                                label = { Text("Nơi cấp") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "Ảnh CCCD (2 mặt)",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            ImageCaptureBox(
                                uri = uiState.idCardFrontUri,
                                label = "Mặt trước",
                                onClick = { launchCamera(ImageType.ID_FRONT) },
                                modifier = Modifier.weight(1f)
                            )
                            ImageCaptureBox(
                                uri = uiState.idCardBackUri,
                                label = "Mặt sau",
                                onClick = { launchCamera(ImageType.ID_BACK) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Step 3: Face Photos
                SormsCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Face,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "3. Ảnh khuôn mặt (3 góc)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "Chụp rõ mặt, không đeo kính, không đội mũ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ImageCaptureBox(
                                uri = uiState.faceFrontUri,
                                label = "Chính diện",
                                onClick = { launchCamera(ImageType.FACE_FRONT) },
                                modifier = Modifier.weight(1f)
                            )
                            ImageCaptureBox(
                                uri = uiState.faceLeftUri,
                                label = "Nghiêng trái",
                                onClick = { launchCamera(ImageType.FACE_LEFT) },
                                modifier = Modifier.weight(1f)
                            )
                            ImageCaptureBox(
                                uri = uiState.faceRightUri,
                                label = "Nghiêng phải",
                                onClick = { launchCamera(ImageType.FACE_RIGHT) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Submit buttons
                Spacer(Modifier.height(8.dp))

                if (!uiState.isProfileSaved) {
                    SormsButton(
                        onClick = { viewModel.saveProfile() },
                        text = if (uiState.isLoading) "Đang lưu..." else "Lưu thông tin cá nhân",
                        enabled = !uiState.isLoading && viewModel.isProfileComplete(),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFDCFCE7)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF16A34A)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Đã lưu thông tin cá nhân",
                                color = Color(0xFF16A34A),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                if (!uiState.isFaceRegistered) {
                    SormsButton(
                        onClick = { viewModel.registerFace(context) },
                        text = if (uiState.isLoading) "Đang đăng ký..." else "Đăng ký khuôn mặt",
                        enabled = !uiState.isLoading && 
                                 uiState.faceFrontUri != null && 
                                 uiState.faceLeftUri != null && 
                                 uiState.faceRightUri != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFDCFCE7)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF16A34A)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Đã đăng ký khuôn mặt",
                                color = Color(0xFF16A34A),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun VerificationProgress(
    step1Complete: Boolean,
    step2Complete: Boolean,
    step3Complete: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ProgressStep(number = 1, label = "Thông tin", isComplete = step1Complete)
        ProgressStep(number = 2, label = "CCCD", isComplete = step2Complete)
        ProgressStep(number = 3, label = "Khuôn mặt", isComplete = step3Complete)
    }
}

@Composable
private fun ProgressStep(number: Int, label: String, isComplete: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(50))
                .background(
                    if (isComplete) Color(0xFF16A34A) else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isComplete) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Text(
                    text = number.toString(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isComplete) Color(0xFF16A34A) else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ImageCaptureBox(
    uri: Uri?,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .border(
                    width = 2.dp,
                    color = if (uri != null) Color(0xFF16A34A) else MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            if (uri != null) {
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = label,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Chụp",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private enum class ImageType {
    ID_FRONT, ID_BACK, FACE_FRONT, FACE_LEFT, FACE_RIGHT
}

