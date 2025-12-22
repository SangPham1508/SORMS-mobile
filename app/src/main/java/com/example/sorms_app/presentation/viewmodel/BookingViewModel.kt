package com.example.sorms_app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sorms_app.domain.model.Booking
import com.example.sorms_app.domain.repository.BookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class BookingUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val bookingSuccess: Boolean = false,
    val requiresFaceRegistration: Boolean = false,
    val bookings: List<Booking> = emptyList(),
    val cancelSuccess: Boolean = false,
    val cancelError: String? = null
)

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val bookingRepository: BookingRepository
    // Note: FaceManagementViewModel should be injected as a use case or repository
    // For now, we'll simulate the check
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    fun createBooking(
        roomId: Long,
        checkInDate: String,
        checkInTime: String,
        checkOutTime: String,
        guestCount: Int,
        specialRequests: String
    ) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isLoading = true, 
                    errorMessage = null, 
                    bookingSuccess = false,
                    requiresFaceRegistration = false
                ) 
            }
            
            try {
                // Check if user has completed face registration
                // TODO: Implement proper face registration check via repository
                val hasFaceRegistration = true // Simulate for now
                
                if (!hasFaceRegistration) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            requiresFaceRegistration = true,
                            errorMessage = "Bạn cần hoàn thành đăng ký nhận diện (khuôn mặt + CCCD) trước khi có thể đặt phòng."
                        ) 
                    }
                    return@launch
                }
                
                // Format dates for API
                val checkInDateTime = "$checkInDate $checkInTime:00"
                val checkOutDateTime = "$checkInDate $checkOutTime:00"
                
                // Call repository to create booking
                // Note: This would need to be implemented in BookingRepository
                // For now, simulate success after delay
                kotlinx.coroutines.delay(2000)
                
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        bookingSuccess = true
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = e.message ?: "Không thể tạo booking"
                    ) 
                }
            }
        }
    }

    fun loadAllBookings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                // TODO: Implement actual booking loading from repository
                // For now, simulate empty list
                kotlinx.coroutines.delay(1000)
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        bookings = emptyList() // Will be populated from repository
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = e.message ?: "Không thể tải danh sách booking"
                    ) 
                }
            }
        }
    }

    fun cancelBooking(bookingId: Long) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    cancelError = null,
                    cancelSuccess = false
                ) 
            }
            
            try {
                // TODO: Implement actual booking cancellation
                kotlinx.coroutines.delay(1000)
                
                // Remove booking from local list
                val updatedBookings = _uiState.value.bookings.filter { it.id != bookingId }
                
                _uiState.update { 
                    it.copy(
                        bookings = updatedBookings,
                        cancelSuccess = true
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        cancelError = e.message ?: "Không thể hủy booking"
                    ) 
                }
            }
        }
    }

    fun updateBooking(
        bookingId: Long,
        checkInDate: Date,
        checkOutDate: Date,
        numGuests: Int,
        note: String?
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                // TODO: Implement actual booking update
                kotlinx.coroutines.delay(1000)
                
                // Update booking in local list
                val updatedBookings = _uiState.value.bookings.map { booking ->
                    if (booking.id == bookingId) {
                        booking.copy(
                            checkInDate = checkInDate,
                            checkOutDate = checkOutDate,
                            numberOfGuests = numGuests,
                            numGuests = numGuests,
                            notes = note,
                            note = note
                        )
                    } else {
                        booking
                    }
                }
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        bookings = updatedBookings
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = e.message ?: "Không thể cập nhật booking"
                    ) 
                }
            }
        }
    }

    fun clearCancelState() {
        _uiState.update { 
            it.copy(
                cancelSuccess = false,
                cancelError = null
            ) 
        }
    }

    fun clearState() {
        _uiState.update { BookingUiState() }
    }
}