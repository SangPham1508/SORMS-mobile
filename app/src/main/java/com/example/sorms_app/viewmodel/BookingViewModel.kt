package com.example.sorms_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sorms_app.data.model.BookingResponse
import com.example.sorms_app.data.model.BookingStatus
import com.example.sorms_app.data.repository.BookingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * ViewModel for managing booking state and operations
 */
class BookingViewModel : ViewModel() {

    private val repository = BookingRepository.getInstance()

    // UI States
    private val _bookingsState = MutableStateFlow<BookingsUiState>(BookingsUiState.Loading)
    val bookingsState: StateFlow<BookingsUiState> = _bookingsState.asStateFlow()

    private val _bookingDetailState = MutableStateFlow<BookingDetailUiState>(BookingDetailUiState.Idle)
    val bookingDetailState: StateFlow<BookingDetailUiState> = _bookingDetailState.asStateFlow()

    private val _createBookingState = MutableStateFlow<CreateBookingUiState>(CreateBookingUiState.Idle)
    val createBookingState: StateFlow<CreateBookingUiState> = _createBookingState.asStateFlow()

    private val _checkinState = MutableStateFlow<CheckinUiState>(CheckinUiState.Idle)
    val checkinState: StateFlow<CheckinUiState> = _checkinState.asStateFlow()

    /**
     * Load bookings for a specific user
     */
    fun loadBookingsByUser(userId: Long) {
        viewModelScope.launch {
            _bookingsState.value = BookingsUiState.Loading
            
            repository.getBookingsByUser(userId)
                .onSuccess { bookings ->
                    _bookingsState.value = BookingsUiState.Success(bookings)
                }
                .onFailure { error ->
                    _bookingsState.value = BookingsUiState.Error(error.message ?: "Unknown error")
                }
        }
    }

    /**
     * Load a specific booking by ID
     */
    fun loadBookingById(bookingId: Long) {
        viewModelScope.launch {
            _bookingDetailState.value = BookingDetailUiState.Loading
            
            repository.getBookingById(bookingId)
                .onSuccess { booking ->
                    _bookingDetailState.value = BookingDetailUiState.Success(booking)
                }
                .onFailure { error ->
                    _bookingDetailState.value = BookingDetailUiState.Error(error.message ?: "Unknown error")
                }
        }
    }

    /**
     * Create a new booking
     */
    fun createBooking(
        userId: String,
        roomId: Long,
        checkinDate: String,
        checkoutDate: String,
        numGuests: Int,
        note: String?
    ) {
        viewModelScope.launch {
            _createBookingState.value = CreateBookingUiState.Loading
            
            // Generate unique booking code
            val code = generateBookingCode()
            
            // Convert date format from dd/MM/yyyy to yyyy-MM-dd
            val formattedCheckinDate = convertDateFormat(checkinDate)
            val formattedCheckoutDate = convertDateFormat(checkoutDate)
            
            repository.createBooking(
                code = code,
                userId = userId,
                roomId = roomId,
                checkinDate = formattedCheckinDate,
                checkoutDate = formattedCheckoutDate,
                numGuests = numGuests,
                note = note
            )
                .onSuccess { booking ->
                    _createBookingState.value = CreateBookingUiState.Success(booking)
                }
                .onFailure { error ->
                    _createBookingState.value = CreateBookingUiState.Error(error.message ?: "Unknown error")
                }
        }
    }

    /**
     * Check-in to a booking
     */
    fun checkinBooking(bookingId: Long, userId: String, faceImageFile: File? = null) {
        viewModelScope.launch {
            _checkinState.value = CheckinUiState.Loading
            
            repository.checkinBooking(bookingId, userId, faceImageFile)
                .onSuccess { checkin ->
                    _checkinState.value = CheckinUiState.Success(
                        bookingId = checkin.bookingId,
                        roomCode = checkin.roomCode ?: "",
                        checkinAt = checkin.checkinAt ?: ""
                    )
                }
                .onFailure { error ->
                    _checkinState.value = CheckinUiState.Error(error.message ?: "Unknown error")
                }
        }
    }

    /**
     * Reset create booking state
     */
    fun resetCreateBookingState() {
        _createBookingState.value = CreateBookingUiState.Idle
    }

    /**
     * Reset checkin state
     */
    fun resetCheckinState() {
        _checkinState.value = CheckinUiState.Idle
    }

    /**
     * Generate a unique booking code
     */
    private fun generateBookingCode(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val datePart = dateFormat.format(Date())
        val uniquePart = UUID.randomUUID().toString().take(6).uppercase()
        return "BK$datePart$uniquePart"
    }

    /**
     * Convert date from dd/MM/yyyy to yyyy-MM-dd format
     */
    private fun convertDateFormat(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }
}

// UI State classes
sealed class BookingsUiState {
    object Loading : BookingsUiState()
    data class Success(val bookings: List<BookingResponse>) : BookingsUiState()
    data class Error(val message: String) : BookingsUiState()
}

sealed class BookingDetailUiState {
    object Idle : BookingDetailUiState()
    object Loading : BookingDetailUiState()
    data class Success(val booking: BookingResponse) : BookingDetailUiState()
    data class Error(val message: String) : BookingDetailUiState()
}

sealed class CreateBookingUiState {
    object Idle : CreateBookingUiState()
    object Loading : CreateBookingUiState()
    data class Success(val booking: BookingResponse) : CreateBookingUiState()
    data class Error(val message: String) : CreateBookingUiState()
}

sealed class CheckinUiState {
    object Idle : CheckinUiState()
    object Loading : CheckinUiState()
    data class Success(
        val bookingId: Long,
        val roomCode: String,
        val checkinAt: String
    ) : CheckinUiState()
    data class Error(val message: String) : CheckinUiState()
}

// Extension functions for BookingResponse
fun BookingResponse.getStatusDisplayText(): String {
    return when (status) {
        BookingStatus.PENDING -> "Đang chờ duyệt"
        BookingStatus.APPROVED -> "Đã xác nhận"
        BookingStatus.REJECTED -> "Bị từ chối"
        BookingStatus.CANCELLED -> "Đã hủy"
        BookingStatus.CHECKED_IN -> "Đang ở"
        BookingStatus.CHECKED_OUT -> "Đã trả phòng"
    }
}

fun BookingResponse.isActive(): Boolean {
    return status == BookingStatus.APPROVED || status == BookingStatus.CHECKED_IN
}

