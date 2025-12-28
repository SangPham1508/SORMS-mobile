package com.example.sorms_app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sorms_app.data.datasource.local.AuthSession
import com.example.sorms_app.data.datasource.remote.FaceRecognitionApiService
import com.example.sorms_app.data.datasource.remote.RetrofitClient
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
    val activeBookings: List<Booking> = emptyList(),  // Đồng bộ với web: thêm active bookings list
    val pendingBookings: List<Booking> = emptyList(),  // Đồng bộ với web: thêm pending bookings list
    val notifications: List<Notification> = emptyList(),
    val activeBookingsCount: Int = 0,
    val pendingBookingsCount: Int = 0,  // Đồng bộ với web: thêm pending count
    val serviceOrdersCount: Int = 0,
    val unpaidOrdersCount: Int = 0,
    val faceRegistrationStatus: Boolean? = null,  // Đồng bộ với web: thêm face registration status
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
                
                val activeBookings = allBookings.filter { booking ->
                    booking.status.equals("CHECKED_IN", ignoreCase = true) || 
                    booking.status.equals("APPROVED", ignoreCase = true)
                }.sortedByDescending { booking ->
                    // Sort by checkInDate descending (newest first) - đồng bộ với web
                    booking.checkInDate?.time ?: 0L
                }
                val activeBookingsCount = activeBookings.size
                
                // Load pending bookings (đồng bộ với web)
                val pendingBookings = allBookings.filter { booking ->
                    booking.status.equals("PENDING", ignoreCase = true)
                }.sortedByDescending { booking ->
                    // Sort by checkInDate descending (newest first) - đồng bộ với web
                    booking.checkInDate?.time ?: 0L
                }
                val pendingBookingsCount = pendingBookings.size
                
                // Load face registration status (đồng bộ với web)
                val faceRegistrationStatus = try {
                    val userId = AuthSession.accountId ?: "0"
                    val response = RetrofitClient.faceRecognitionApiService.getFaceStatus(userId)
                    if (response.isSuccessful) {
                        response.body()?.data?.registered ?: false
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    null
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
                    activeBookings = activeBookings,  // Đồng bộ với web: thêm active bookings list
                    pendingBookings = pendingBookings,  // Đồng bộ với web: thêm pending bookings list
                    notifications = notifications,
                    activeBookingsCount = activeBookingsCount,
                    pendingBookingsCount = pendingBookingsCount,  // Đồng bộ với web: thêm pending count
                    serviceOrdersCount = serviceOrdersCount,
                    unpaidOrdersCount = unpaidOrdersCount,
                    faceRegistrationStatus = faceRegistrationStatus,  // Đồng bộ với web: thêm face status
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
