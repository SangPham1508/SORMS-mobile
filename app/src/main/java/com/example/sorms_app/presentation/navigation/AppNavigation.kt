package com.example.sorms_app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sorms_app.presentation.screens.login.LoginScreen
import com.example.sorms_app.presentation.screens.staff.StaffMainScreen
import com.example.sorms_app.presentation.screens.user.UserMainScreen
import com.example.sorms_app.presentation.viewmodel.AuthUiState
import com.example.sorms_app.presentation.viewmodel.AuthViewModel

object Routes {
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

    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onGoogleSignInClick = onGoogleSignInClick,
                errorMessage = if (authState is AuthUiState.Error) (authState as AuthUiState.Error).message else null
            )
        }

        composable(Routes.USER_MAIN) {
            UserMainScreen(onLogout = onLogout)
        }

        composable(Routes.STAFF_MAIN) {
            StaffMainScreen(onLogout = onLogout)
        }
    }

    // Handle navigation based on auth state
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthUiState.Success -> {
                val destination = if (state.roles.any { it.equals("STAFF", ignoreCase = true) }) {
                    Routes.STAFF_MAIN
                } else {
                    Routes.USER_MAIN
                }
                navController.navigate(destination) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            }
            else -> {
                // Handle other states if necessary, e.g., navigate back to login on logout
                if (navController.currentDestination?.route != Routes.LOGIN) {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true } // Clear back stack
                    }
                }
            }
        }
    }
}
