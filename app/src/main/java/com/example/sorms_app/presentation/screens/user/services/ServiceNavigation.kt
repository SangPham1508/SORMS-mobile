package com.example.sorms_app.presentation.screens.user.services

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sorms_app.presentation.viewmodel.ServiceViewModel

object ServiceRoutes {
    const val SERVICE_LIST = "service_list"
    const val SERVICE_REQUEST = "service_request/{serviceId}"

    fun serviceRequest(serviceId: String) = "service_request/$serviceId"
}

@Composable
fun ServiceNavigation() {
    val navController = rememberNavController()
    val viewModel: ServiceViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = ServiceRoutes.SERVICE_LIST) {
        composable(ServiceRoutes.SERVICE_LIST) {
            ServicesScreen(
                viewModel = viewModel,
                onServiceClick = { serviceId ->
                    navController.navigate(ServiceRoutes.serviceRequest(serviceId))
                }
            )
        }
        composable(
            route = ServiceRoutes.SERVICE_REQUEST,
            arguments = listOf(navArgument("serviceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId") ?: return@composable
            ServiceRequestScreen(
                serviceId = serviceId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onSubmissionSuccess = { navController.popBackStack() } // Navigate back on success
            )
        }
    }
}
