package com.example.sorms_app.presentation.screens.staff

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Task
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
import com.example.sorms_app.presentation.screens.staff.schedule.ScheduleScreen
import com.example.sorms_app.presentation.screens.staff.tasks.TaskNavigation
import com.example.sorms_app.presentation.screens.user.profile.ProfileScreen

sealed class StaffScreen(val route: String, val label: String, val icon: ImageVector) {
    object Tasks : StaffScreen("tasks", "Công việc", Icons.Default.Task)
    object Schedule : StaffScreen("schedule", "Lịch", Icons.Default.CalendarMonth)
    object Profile : StaffScreen("profile", "Tài khoản", Icons.Default.AccountCircle)
}

val staffScreens = listOf(
    StaffScreen.Tasks,
    StaffScreen.Schedule,
    StaffScreen.Profile,
)

@Composable
fun StaffMainScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                staffScreens.forEach { screen ->
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
            startDestination = StaffScreen.Tasks.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(StaffScreen.Tasks.route) { 
                TaskNavigation()
            } 
            composable(StaffScreen.Schedule.route) { ScheduleScreen() }
            composable(StaffScreen.Profile.route) { 
                StaffProfileScreen(onLogout = onLogout)
            }
        }
    }
}
