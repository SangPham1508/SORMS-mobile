package com.example.sorms_app.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sorms_app.data.utils.RoleUtils
import com.example.sorms_app.presentation.screens.LoginScreen
import com.example.sorms_app.presentation.viewmodel.AuthUiState
import com.example.sorms_app.presentation.viewmodel.AuthViewModel

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val USER_MAIN = "user_main"
    const val STAFF_MAIN = "staff_main"
}

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    onGoogleSignInClick: () -> Unit,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val authState by authViewModel.uiState.collectAsState()

    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) {
            SplashScreen()
        }
        
        composable(Routes.LOGIN) {
            LoginScreen(
                onGoogleSignInClick = onGoogleSignInClick,
                errorMessage = if (authState is AuthUiState.Error) (authState as AuthUiState.Error).message else null
            )
        }

        composable(Routes.USER_MAIN) {
            UserNavigation(onLogout = onLogout)
        }

        composable(Routes.STAFF_MAIN) {
            StaffNavigation(onLogout = onLogout)
        }
    }

    // Handle navigation based on auth state
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthUiState.CheckingSession -> {
                // Stay on splash screen while checking
            }
            is AuthUiState.Success -> {
                // Đồng bộ với web: sử dụng RoleUtils để map role
                val primaryRole = state.roles.firstOrNull()?.let { RoleUtils.mapRoleToAppRole(it) }
                val destination = when (primaryRole) {
                    com.example.sorms_app.data.utils.AppRole.STAFF -> Routes.STAFF_MAIN
                    com.example.sorms_app.data.utils.AppRole.ADMIN, 
                    com.example.sorms_app.data.utils.AppRole.OFFICE -> Routes.STAFF_MAIN  // Admin và Office dùng staff flow
                    else -> Routes.USER_MAIN
                }
                navController.navigate(destination) {
                    popUpTo(0) { inclusive = true }
                }
            }
            is AuthUiState.Idle, is AuthUiState.Error -> {
                // Go to login screen
                if (navController.currentDestination?.route != Routes.LOGIN) {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            is AuthUiState.Loading -> {
                // Stay on current screen while loading
            }
        }
    }
}

@Composable
private fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF5C33D6),
                        Color(0xFF1A0B4D)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SORMS",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Hệ thống quản lý nhà công vụ",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = Color.White,
                strokeWidth = 3.dp
            )
        }
    }
}
