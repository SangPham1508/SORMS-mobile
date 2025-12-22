package com.example.sorms_app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sorms_app.data.datasource.local.AuthSession
import com.example.sorms_app.domain.model.Booking
import com.example.sorms_app.domain.model.Notification
import com.example.sorms_app.domain.repository.BookingRepository
import com.example.sorms_app.domain.repository.NotificationRepository
import com.example.sorms_app.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserDashboardUiState(
    val isLoading: Boolean = false,
    val userName: String = "Người dùng",
    val currentBooking: Booking? = null,
    val notifications: List<Notification> = emptyList(),
    val activeBookingsCount: Int = 0,
    val serviceOrdersCount: Int = 0,
    val unpaidOrdersCount: Int = 0,
    val errorMessage: String? = null
)

@HiltViewModel
class UserDashboardViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val notificationRepository: NotificationRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserDashboardUiState())
    val uiState: StateFlow<UserDashboardUiState> = _uiState.asStateFlow()

    init {
        // Delay loading to avoid ANR during app startup
        viewModelScope.launch {
            kotlinx.coroutines.delay(300) // Let UI render first
            loadDashboardData()
        }
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                // Get user name from session
                val userName = AuthSession.userName.takeIf { !it.isNullOrBlank() } 
                    ?: AuthSession.userEmail?.substringBefore("@") 
                    ?: "Người dùng"
                
                // Load current booking
                val currentBooking = try {
                    bookingRepository.getCurrentBooking().first()
                } catch (e: Exception) {
                    null
                }
                
                // Load all bookings to count active ones
                val allBookings = try {
                    bookingRepository.getAllBookings().first()
                } catch (e: Exception) {
                    emptyList()
                }
                
                val activeBookingsCount = allBookings.count { booking ->
                    booking.status.equals("CHECKED_IN", ignoreCase = true) || 
                    booking.status.equals("APPROVED", ignoreCase = true)
                }
                
                // Load notifications
                val notifications = try {
                    notificationRepository.getRecentNotifications().first()
                } catch (e: Exception) {
                    emptyList()
                }
                
                // Load service orders count (mock for now)
                val serviceOrdersCount = try {
                    // orderRepository.getUserOrders().first().size
                    3 // Mock data
                } catch (e: Exception) {
                    0
                }
                
                val unpaidOrdersCount = try {
                    // orderRepository.getUnpaidOrders().first().size
                    2 // Mock data
                } catch (e: Exception) {
                    0
                }
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    userName = userName,
                    currentBooking = currentBooking,
                    notifications = notifications,
                    activeBookingsCount = activeBookingsCount,
                    serviceOrdersCount = serviceOrdersCount,
                    unpaidOrdersCount = unpaidOrdersCount,
                    errorMessage = null
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Không thể tải dữ liệu: ${e.message}"
                )
            }
        }
    }
    
    fun refreshData() {
        loadDashboardData()
    }
}
