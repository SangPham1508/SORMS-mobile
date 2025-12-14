package com.example.sorms_app.presentation.screens.staff.tasks

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sorms_app.presentation.screens.staff.dashboard.StaffDashboardScreen

object TaskRoutes {
    const val TASK_LIST = "task_list"
    const val TASK_DETAIL = "task_detail/{taskId}"

    fun taskDetail(taskId: String) = "task_detail/$taskId"
}

@Composable
fun TaskNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = TaskRoutes.TASK_LIST) {
        composable(TaskRoutes.TASK_LIST) {
            StaffDashboardScreen(
                taskViewModel = hiltViewModel(),
                onTaskClick = { taskId ->
                    navController.navigate(TaskRoutes.taskDetail(taskId))
                }
            )
        }
        composable(
            route = TaskRoutes.TASK_DETAIL,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: return@composable
            TaskDetailScreen(
                taskId = taskId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}



