package com.example.sorms_app.presentation.screens.user

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sorms_app.presentation.screens.user.bookings.BookingsScreen
import com.example.sorms_app.presentation.screens.user.dashboard.UserDashboardScreen
import com.example.sorms_app.presentation.screens.user.profile.ProfileScreen
import com.example.sorms_app.presentation.screens.user.services.ServiceNavigation

sealed class UserScreen(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : UserScreen("dashboard", "Trang chủ", Icons.Default.Home)
    object Services : UserScreen("services", "Dịch vụ", Icons.Default.ShoppingCart)
    object Bookings : UserScreen("bookings", "Đặt phòng", Icons.Default.List)
    object Profile : UserScreen("profile", "Tài khoản", Icons.Default.AccountCircle)
}

val userScreens = listOf(
    UserScreen.Dashboard,
    UserScreen.Services,
    UserScreen.Bookings,
    UserScreen.Profile,
)

@Composable
fun UserMainScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                userScreens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = UserScreen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(UserScreen.Dashboard.route) { UserDashboardScreen() }
            composable(UserScreen.Services.route) { ServiceNavigation() }
            composable(UserScreen.Bookings.route) { BookingsScreen() }
            composable(UserScreen.Profile.route) { 
                ProfileScreen(onLogout = onLogout)
            }
        }
    }
}
