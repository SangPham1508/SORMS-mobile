package com.example.sorms_app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sorms_app.domain.model.Booking
import com.example.sorms_app.domain.repository.BookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
    val isLoading: Boolean = false,
    val bookings: List<Booking> = emptyList(),
    val filteredBookings: List<Booking> = emptyList(),
    val selectedFilter: String = "Tất cả",
    val errorMessage: String? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    fun loadHistory() {
        viewModelScope.launch {
            bookingRepository.getUserBookings()
                .onStart { 
                    _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                }
                .catch { e -> 
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = e.message ?: "Không thể tải lịch sử booking"
                        ) 
                    }
                }
                .collect { bookings ->
                    // Sort by check-in date descending (newest first)
                    val sortedBookings = bookings.sortedByDescending { it.checkInDate }
                    
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            bookings = sortedBookings,
                            filteredBookings = filterBookings(sortedBookings, it.selectedFilter)
                        ) 
                    }
                }
        }
    }

    fun setFilter(filter: String) {
        _uiState.update { 
            it.copy(
                selectedFilter = filter,
                filteredBookings = filterBookings(it.bookings, filter)
            ) 
        }
    }

    private fun filterBookings(bookings: List<Booking>, filter: String): List<Booking> {
        return when (filter) {
            "Hoàn thành" -> bookings.filter { it.status.equals("COMPLETED", ignoreCase = true) }
            "Đã hủy" -> bookings.filter { it.status.equals("CANCELLED", ignoreCase = true) }
            "Đang diễn ra" -> bookings.filter { 
                it.status.equals("CONFIRMED", ignoreCase = true) || 
                it.status.equals("CHECKED_IN", ignoreCase = true) 
            }
            else -> bookings // "Tất cả"
        }
    }

    fun refresh() {
        loadHistory()
    }
}