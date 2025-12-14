package com.example.sorms_app.presentation.screens.user.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sorms_app.presentation.components.SormsEmptyState
import com.example.sorms_app.presentation.viewmodel.UserViewModel

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    userViewModel: UserViewModel = viewModel()
) {
    val uiState by userViewModel.uiState.collectAsState()
    var showConfirmLogout by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5F8))
            .padding(16.dp)
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            uiState.errorMessage != null -> {
                SormsEmptyState(
                    title = "Lỗi",
                    subtitle = uiState.errorMessage ?: "Không thể tải thông tin người dùng."
                )
            }
            uiState.user != null -> {
                val user = uiState.user!!
                Column {
                    Text(text = "Hồ sơ", style = MaterialTheme.typography.titleLarge)

                    Spacer(Modifier.height(12.dp))
                    Divider(color = Color(0xFFE5E7EB))
                    Spacer(Modifier.height(12.dp))

                    Text(text = "Tên: ${user.name}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(text = "Email: ${user.email}", style = MaterialTheme.typography.bodyMedium)

                    Spacer(Modifier.height(24.dp))

                    Button(onClick = { showConfirmLogout = true }) {
                        Text("Đăng xuất")
                    }
                }
            }
        }
    }

    if (showConfirmLogout) {
        AlertDialog(
            onDismissRequest = { showConfirmLogout = false },
            title = { Text("Xác nhận đăng xuất") },
            text = { Text("Bạn có chắc chắn muốn đăng xuất?") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmLogout = false
                    onLogout()
                }) { Text("Đăng xuất") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmLogout = false }) { Text("Hủy") }
            }
        )
    }
}
