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
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookingsUiState(
    val isLoading: Boolean = false,
    val bookings: List<Booking> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingsUiState())
    val uiState: StateFlow<BookingsUiState> = _uiState.asStateFlow()

    init {
        loadAllBookings()
    }

    fun loadAllBookings() {
        viewModelScope.launch {
            bookingRepository.getAllBookings()
                .onStart { _uiState.value = BookingsUiState(isLoading = true) }
                .catch { e -> _uiState.value = BookingsUiState(errorMessage = e.message) }
                .collect { bookings ->
                    _uiState.value = BookingsUiState(bookings = bookings)
                }
        }
    }
}
