package com.example.sorms_app.presentation.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.sorms_app.R
import com.example.sorms_app.presentation.components.ButtonVariant
import com.example.sorms_app.presentation.theme.Blue400
import com.example.sorms_app.presentation.theme.DesignSystem
import com.example.sorms_app.presentation.theme.LoginBackground
import com.example.sorms_app.presentation.theme.SORMS_appTheme
import com.example.sorms_app.presentation.theme.Gray500
import com.example.sorms_app.presentation.theme.Gray900

@Composable
fun LoginScreen(
    onGoogleSignInClick: () -> Unit,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    val isPreviewMode = LocalInspectionMode.current
    val context = if (!isPreviewMode) {
        LocalContext.current
    } else {
        null // Preview mode - context not available
    }
    
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Background Color - #EAF4FF
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LoginBackground)
                .zIndex(0f)
        )
        
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = DesignSystem.Spacing.lg)
                .zIndex(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo Section with blur effect
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .padding(bottom = DesignSystem.Spacing.xl),
                contentAlignment = Alignment.Center
            ) {
                // Blur effect background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = Blue400.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(70.dp)
                        )
                        .blur(32.dp)
                        .scale(1.5f)
                )
                
                // White box with logo
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(20.dp),
                            spotColor = Color.Black.copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "SORMS Logo",
                        modifier = Modifier
                            .size(120.dp)
                            .padding(10.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(DesignSystem.Spacing.sm))

            // Welcome Text - "Chào mừng đến với"
            Text(
                text = "Chào mừng đến với",
                fontSize = DesignSystem.Typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = Gray500,
                textAlign = TextAlign.Center,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(DesignSystem.Spacing.xs))

            // App Name - "SORMS" (larger and bolder)
            Text(
                text = "SORMS",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Gray900,
                textAlign = TextAlign.Center,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(DesignSystem.Spacing.sm))

            // App Description
            Text(
                text = "Hệ thống quản lý đặt phòng, dịch vụ và thanh toán một cách thông minh",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Gray500,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = DesignSystem.Spacing.xl)
            )

            Spacer(modifier = Modifier.height(DesignSystem.Spacing.xxl))

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

            // Google Sign In Button with Google Logo
            Button(
                onClick = onGoogleSignInClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(8.dp),
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    ),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Google Logo in white box
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_google_logo),
                            contentDescription = "Google Logo",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = "Đăng nhập bằng Google",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

        }
        
        // Legal Disclaimer at bottom
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = DesignSystem.Spacing.xl)
                .padding(bottom = DesignSystem.Spacing.xl)
                .zIndex(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Legal Disclaimer with clickable links
            val privacyPolicyText = buildAnnotatedString {
                val baseStyle = SpanStyle(
                    color = Gray500,
                    fontSize = 10.sp
                )
                val linkStyle = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.Underline
                )
                
                withStyle(baseStyle) {
                    append("Bằng việc tiếp tục, bạn đồng ý với ")
                }
                pushStringAnnotation(tag = "privacy", annotation = "https://sorms-web.vercel.app/")
                withStyle(linkStyle) {
                    append("Chính sách bảo mật")
                }
                pop()
                withStyle(baseStyle) {
                    append(" và ")
                }
                pushStringAnnotation(tag = "terms", annotation = "https://sorms-web.vercel.app/")
                withStyle(linkStyle) {
                    append("Điều khoản dịch vụ")
                }
                pop()
                withStyle(baseStyle) {
                    append(" của chúng tôi.")
                }
            }
            
            ClickableText(
                text = privacyPolicyText,
                style = MaterialTheme.typography.bodySmall.copy(
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp
                ),
                modifier = Modifier.padding(horizontal = DesignSystem.Spacing.lg),
                onClick = { offset ->
                    privacyPolicyText.getStringAnnotations(
                        tag = "privacy",
                        start = offset,
                        end = offset
                    ).firstOrNull()?.let {
                        context?.let { ctx ->
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.item))
                            ctx.startActivity(intent)
                        }
                    }
                    privacyPolicyText.getStringAnnotations(
                        tag = "terms",
                        start = offset,
                        end = offset
                    ).firstOrNull()?.let {
                        context?.let { ctx ->
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.item))
                            ctx.startActivity(intent)
                        }
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true, name = "Login Screen")
@Composable
private fun LoginScreenPreview() {
    SORMS_appTheme {
        LoginScreen(
            onGoogleSignInClick = {},
            errorMessage = null
        )
    }
}

@Preview(showBackground = true, name = "Login Screen with Error")
@Composable
private fun LoginScreenWithErrorPreview() {
    SORMS_appTheme {
        LoginScreen(
            onGoogleSignInClick = {},
            errorMessage = "Đăng nhập thất bại. Vui lòng thử lại."
        )
    }
}