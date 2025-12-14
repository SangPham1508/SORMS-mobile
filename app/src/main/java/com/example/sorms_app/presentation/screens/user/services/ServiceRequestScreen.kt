package com.example.sorms_app.presentation.screens.user.services

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.sorms_app.presentation.components.SormsButton
import com.example.sorms_app.presentation.viewmodel.ServiceViewModel

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

    // Find the service details from the list (already loaded in the ViewModel)
    val service = uiState.services.find { it.id == serviceId }
    var notes by remember { mutableStateOf("") }

    // Handle submission success
    LaunchedEffect(uiState.submissionSuccess) {
        if (uiState.submissionSuccess) {
            Toast.makeText(context, "Yêu cầu dịch vụ đã được gửi thành công!", Toast.LENGTH_SHORT).show()
            viewModel.resetSubmissionState() // Reset state before navigating back
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
        },
        bottomBar = {
            SormsButton(
                onClick = { 
                    service?.let { 
                        viewModel.createServiceRequest(it.id, notes) 
                    }
                },
                text = if (uiState.isSubmitting) "Đang gửi..." else "Gửi yêu cầu",
                enabled = !uiState.isSubmitting,
                modifier = Modifier.padding(16.dp)
            )
        }
    ) { innerPadding ->
        if (service == null) {
            // Show an error or loading state if service details are not found
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Không tìm thấy thông tin dịch vụ.")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Dịch vụ: ${service.name}",
                    style = MaterialTheme.typography.headlineSmall
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Ghi chú (tùy chọn)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4
                )
            }
        }
    }
}
