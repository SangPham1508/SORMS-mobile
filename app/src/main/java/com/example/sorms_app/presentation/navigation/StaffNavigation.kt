package com.example.sorms_app.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sorms_app.presentation.screens.staff.StaffDashboardScreen
import com.example.sorms_app.presentation.screens.staff.StaffTasksScreen
import com.example.sorms_app.presentation.screens.staff.StaffOrdersScreen
import com.example.sorms_app.presentation.screens.staff.StaffProfileScreen

enum class StaffTab(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    DASHBOARD("staff_dashboard", "Trang chủ", Icons.Default.Dashboard),
    TASKS("staff_tasks", "Công việc", Icons.Default.Task),
    ORDERS("staff_orders", "Đơn hàng", Icons.Default.Assignment),
    PROFILE("staff_profile", "Cá nhân", Icons.Default.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffNavigation(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            StaffBottomBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(StaffTab.DASHBOARD.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = StaffTab.DASHBOARD.route,
            modifier = modifier.padding(paddingValues)
        ) {
            composable(StaffTab.DASHBOARD.route) {
                StaffDashboardScreen(
                    onNavigateToTasks = { navController.navigate(StaffTab.TASKS.route) },
                    onNavigateToOrders = { navController.navigate(StaffTab.ORDERS.route) },
                    onTaskSelected = { task ->
                        // TODO: Navigate to task detail
                    }
                )
            }
            
            composable(StaffTab.TASKS.route) {
                StaffTasksScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onTaskSelected = { task ->
                        // TODO: Navigate to task detail
                    }
                )
            }
            
            composable(StaffTab.ORDERS.route) {
                StaffOrdersScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onOrderSelected = { order ->
                        // TODO: Navigate to order detail
                    }
                )
            }
            
            composable(StaffTab.PROFILE.route) {
                StaffProfileScreen(
                    onLogout = onLogout
                )
            }
        }
    }
}

@Composable
fun StaffBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar {
        StaffTab.values().forEach { tab ->
            NavigationBarItem(
                selected = currentRoute == tab.route,
                onClick = { onNavigate(tab.route) },
                icon = { 
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label
                    )
                },
                label = { Text(tab.label) }
            )
        }
    }
}