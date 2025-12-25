package com.example.sorms_app.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.sorms_app.R
import com.example.sorms_app.presentation.components.SormsButton
import com.example.sorms_app.presentation.components.SormsCard
import com.example.sorms_app.presentation.components.ButtonVariant
import com.example.sorms_app.presentation.theme.DesignSystem

@Composable
fun LoginScreen(
    onGoogleSignInClick: () -> Unit,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.bg),
            contentDescription = "Background",
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0f),
            contentScale = ContentScale.Crop
        )
        
        // Overlay Gradient để text dễ đọc hơn
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.4f),
                            Color.Black.copy(alpha = 0.6f)
                        )
                    )
                )
                .zIndex(1f)
        )
        
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(DesignSystem.Spacing.lg)
                .zIndex(2f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo Section với logo thật
            Card(
                modifier = Modifier.size(140.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(28.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "SORMS Logo",
                        modifier = Modifier
                            .size(120.dp)
                            .padding(12.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(DesignSystem.Spacing.xl))

            // Title với improved styling
            Text(
                text = "SORMS",
                fontSize = DesignSystem.Typography.displayLarge + 8.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(DesignSystem.Spacing.sm))

            // Subtitle với improved styling
            Text(
                text = "Smart Office Residence\nManagement System",
                fontSize = DesignSystem.Typography.bodyLarge + 2.sp,
                color = Color.White.copy(alpha = 0.95f),
                textAlign = TextAlign.Center,
                lineHeight = DesignSystem.Typography.bodyLarge * 1.4f
            )

            Spacer(modifier = Modifier.height(DesignSystem.Spacing.xxl))

            // Welcome Message Card với improved styling
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.95f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(DesignSystem.Sizes.cardCornerRadius)
            ) {
                Column(
                    modifier = Modifier.padding(DesignSystem.Spacing.cardContentPadding + 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Chào mừng bạn đến với SORMS",
                        fontSize = DesignSystem.Typography.h3,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(DesignSystem.Spacing.sm))
                    
                    Text(
                        text = "Quản lý đặt phòng, dịch vụ và thanh toán một cách thông minh",
                        fontSize = DesignSystem.Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                        textAlign = TextAlign.Center,
                        lineHeight = DesignSystem.Typography.bodyMedium * 1.5f
                    )
                }
            }

            Spacer(modifier = Modifier.height(DesignSystem.Spacing.xl))

            // Error Message
            errorMessage?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = DesignSystem.Spacing.md),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(DesignSystem.Sizes.cardCornerRadius * 0.75f)
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(DesignSystem.Spacing.cardContentPadding),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = DesignSystem.Typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Google Sign In Button
            SormsButton(
                onClick = onGoogleSignInClick,
                text = "Đăng nhập với Google",
                variant = ButtonVariant.Primary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(DesignSystem.Spacing.lg))

            // Footer với improved styling
            Text(
                text = "Đăng nhập bằng tài khoản Google của bạn\nđể truy cập vào hệ thống",
                fontSize = DesignSystem.Typography.bodySmall,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                lineHeight = DesignSystem.Typography.bodySmall * 1.4f
            )
        }
    }
}