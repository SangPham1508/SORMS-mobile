package com.example.sorms_app.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.sorms_app.presentation.screens.user.dashboard.UserDashboardScreen
import com.example.sorms_app.presentation.screens.user.rooms.RoomsScreen
import com.example.sorms_app.presentation.screens.user.services.ServicesScreen
import com.example.sorms_app.presentation.screens.user.oder.OrdersScreen
import com.example.sorms_app.presentation.screens.user.profile.ProfileScreen
import com.example.sorms_app.presentation.screens.user.bookings.BookingDetailScreen
import com.example.sorms_app.presentation.screens.user.services.ServiceDetailScreen
import com.example.sorms_app.presentation.screens.user.oder.OrderDetailScreen
import com.example.sorms_app.presentation.screens.user.services.CartScreen
import com.example.sorms_app.presentation.screens.user.verification.FaceRegisterScreen
import com.example.sorms_app.presentation.screens.user.verification.FaceManagementScreen
import com.example.sorms_app.presentation.screens.user.history.HistoryScreen

enum class UserTab(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    DASHBOARD("dashboard", "Trang chủ", Icons.Default.Home),
    ROOMS("rooms", "Phòng", Icons.Default.Business),
    SERVICES("services", "Dịch vụ", Icons.Default.CleaningServices),
    ORDERS("orders", "Đơn hàng", Icons.Default.Receipt),
    PROFILE("profile", "Cá nhân", Icons.Default.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserNavigation(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            UserBottomBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(UserTab.DASHBOARD.route) {
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
            startDestination = UserTab.DASHBOARD.route,
            modifier = modifier.padding(paddingValues)
        ) {
            composable(UserTab.DASHBOARD.route) {
                UserDashboardScreen(
                    onNavigateToRooms = { navController.navigate(UserTab.ROOMS.route) },
                    onNavigateToServices = { navController.navigate(UserTab.SERVICES.route) },
                    onNavigateToOrders = { navController.navigate(UserTab.ORDERS.route) },
                    onNavigateToHistory = { navController.navigate("history") },
                    onNavigateToFaceRegister = { navController.navigate("face_management") }
                )
            }
            
            composable(UserTab.ROOMS.route) {
                RoomsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToFaceRegister = { navController.navigate("face_management") },
                    onBookingSuccess = {
                        // Navigate to orders and clear the back stack up to the dashboard
                        navController.navigate(UserTab.ORDERS.route) {
                            popUpTo(UserTab.DASHBOARD.route)
                        }
                    }
                )
            }
            
            composable(UserTab.SERVICES.route) {
                ServicesScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToOrders = {
                        navController.navigate(UserTab.ORDERS.route) {
                            popUpTo(UserTab.DASHBOARD.route)
                        }
                    },
                    onServiceSelected = { service ->
                        navController.navigate("service_detail/${service.id}")
                    },
                    onViewCart = {
                        navController.navigate("cart")
                    }
                )
            }
            
            composable(UserTab.ORDERS.route) {
                OrdersScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onOrderSelected = { order ->
                        navController.navigate("order_detail/${order.id}")
                    }
                )
            }
            
            composable(UserTab.PROFILE.route) {
                ProfileScreen(
                    onLogout = onLogout,
                    onNavigateToFaceManagement = {
                        navController.navigate("face_management")
                    }
                )
            }
            
            // Detail Screens
            composable(
                "booking_detail/{roomId}",
                arguments = listOf(navArgument("roomId") { type = NavType.LongType })
            ) { backStackEntry ->
                val roomId = backStackEntry.arguments?.getLong("roomId") ?: 0L
                // TODO: Get room data by ID
                // For now, create a mock room
                val mockRoom = com.example.sorms_app.data.models.RoomData(
                    id = roomId,
                    name = "Phòng $roomId",
                    code = "R-$roomId",
                    roomTypeName = "Standard",
                    description = "Phòng tiêu chuẩn",
                    number = roomId.toString(),
                    status = "AVAILABLE",
                    floor = 1,
                    capacity = "4",
                    isAvailable = true,
                    type = "STANDARD"
                )
                
                BookingDetailScreen(
                    room = mockRoom,
                    onNavigateBack = { navController.popBackStack() },
                    onBookingSuccess = { 
                        navController.popBackStack()
                        navController.navigate(UserTab.ORDERS.route)
                    }
                )
            }
            
            composable(
                "service_detail/{serviceId}",
                arguments = listOf(navArgument("serviceId") { type = NavType.StringType })
            ) { backStackEntry ->
                val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""
                // TODO: Get service data by ID
                // For now, create a mock service
                val mockService = com.example.sorms_app.domain.model.Service(
                    id = serviceId,
                    name = "Dịch vụ $serviceId",
                    code = "SV-$serviceId",
                    description = "Mô tả dịch vụ",
                    unitPrice = 100000.0,
                    unitName = "lần",
                    isActive = true,
                    icon = Icons.Default.CleaningServices
                )
                
                ServiceDetailScreen(
                    service = mockService,
                    onNavigateBack = { navController.popBackStack() },
                    onRequestSuccess = { 
                        navController.popBackStack()
                        navController.navigate(UserTab.ORDERS.route)
                    }
                )
            }
            
            composable(
                "order_detail/{orderId}",
                arguments = listOf(navArgument("orderId") { type = NavType.LongType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getLong("orderId") ?: 0L
                
                OrderDetailScreen(
                    orderId = orderId,
                    onNavigateBack = { navController.popBackStack() },
                    onPaymentSuccess = { 
                        navController.popBackStack()
                    }
                )
            }
            
            composable("cart") {
                CartScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onCheckoutSuccess = { 
                        navController.popBackStack()
                        navController.navigate(UserTab.ORDERS.route)
                    }
                )
            }
            
            composable("face_register") {
                FaceRegisterScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onRegistrationSuccess = { 
                        navController.popBackStack()
                    }
                )
            }
            
            composable("face_management") {
                FaceManagementScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToRegister = { 
                        navController.navigate("face_register")
                    }
                )
            }
            
            composable("history") {
                HistoryScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onBookingSelected = { booking ->
                        // Navigate to booking detail or show booking info
                        navController.navigate("booking_detail/${booking.id}")
                    }
                )
            }
        }
    }
}

@Composable
fun UserBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,  // Đồng bộ với design system - màu trắng/nền
        contentColor = MaterialTheme.colorScheme.onSurface  // Đồng bộ màu text/icon
    ) {
        UserTab.values().forEach { tab ->
            NavigationBarItem(
                selected = currentRoute == tab.route,
                onClick = { onNavigate(tab.route) },
                icon = { 
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label
                    )
                },
                label = { Text(tab.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,  // Màu primary khi selected
                    selectedTextColor = MaterialTheme.colorScheme.primary,  // Màu primary khi selected
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),  // Indicator màu nhẹ
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),  // Màu nhẹ khi unselected
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)  // Màu nhẹ khi unselected
                )
            )
        }
    }
}


