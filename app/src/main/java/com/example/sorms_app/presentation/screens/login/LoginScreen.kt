package com.example.sorms_app.presentation.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sorms_app.R
import com.example.sorms_app.presentation.theme.SORMS_appTheme

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onGoogleSignInClick: () -> Unit = {},
    errorMessage: String? = null
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF5C33D6), // Màu tím sáng hơn
                        Color(0xFF1A0B4D)  // Màu tím đậm hơn
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f),
            shape = RoundedCornerShape(28.dp), // Bo góc nhiều hơn một chút
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 40.dp), // Tăng padding dọc
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "SORMS logo",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(72.dp) // Tăng kích thước logo
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Chào mừng đến với SORMS", // Thay đổi văn bản cho thân thiện hơn
                    style = MaterialTheme.typography.headlineSmall.copy( // Dùng style có sẵn
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Hệ thống quản lý nhà công vụ thông minh",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Vùng hiển thị lỗi được cải tiến
                if (!errorMessage.isNullOrBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                        )
                    ) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }


                // Nút đăng nhập Google
                Button(
                    onClick = onGoogleSignInClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50), // Bo tròn hoàn toàn
                    contentPadding = PaddingValues(vertical = 14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4285F4), // Màu xanh của Google
                        contentColor = Color.White
                    )
                ) {
                    // Thêm Icon Google chính thức
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google_logo), // <-- THÊM ICON NÀY VÀO DRAWABLE
                        contentDescription = "Google logo",
                        modifier = Modifier.size(20.dp),
                        tint = Color.Unspecified
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Đăng nhập với Google",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_4")
@Composable
fun LoginScreenPreview() {
    SORMS_appTheme {
        LoginScreen(errorMessage = "Đăng nhập thất bại, vui lòng thử lại.")
    }
}

@Preview(showBackground = true, device = "id:pixel_4")
@Composable
fun LoginScreenPreviewNoError() {
    SORMS_appTheme {
        LoginScreen()
    }
}


