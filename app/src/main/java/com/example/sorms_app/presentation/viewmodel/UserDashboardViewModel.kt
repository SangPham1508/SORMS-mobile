package com.example.sorms_app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sorms_app.domain.model.Booking
import com.example.sorms_app.domain.model.Notification
import com.example.sorms_app.domain.repository.BookingRepository
import com.example.sorms_app.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserDashboardUiState(
    val isLoading: Boolean = false,
    val currentBooking: Booking? = null,
    val notifications: List<Notification> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class UserDashboardViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserDashboardUiState())
    val uiState: StateFlow<UserDashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            combine(
                bookingRepository.getCurrentBooking(),
                notificationRepository.getRecentNotifications()
            ) { booking, notifications ->
                UserDashboardUiState(currentBooking = booking, notifications = notifications)
            }
            .onStart { _uiState.value = UserDashboardUiState(isLoading = true) }
            .catch { e -> _uiState.value = UserDashboardUiState(errorMessage = e.message) }
            .collect { combinedState ->
                _uiState.value = combinedState
            }
        }
    }
}
