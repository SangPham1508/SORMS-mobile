package com.example.sorms_app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sorms_app.data.datasource.local.AuthSession
import com.example.sorms_app.data.datasource.remote.BookingApiService
import com.example.sorms_app.data.datasource.remote.CreateBookingRequest
import com.example.sorms_app.data.models.RoomData
import com.example.sorms_app.data.models.RoomTypeResponse
import com.example.sorms_app.data.models.toRoomData
import com.example.sorms_app.data.repository.Result
import com.example.sorms_app.data.repository.RoomRepository
import com.example.sorms_app.data.repository.RoomTypeRepository
import com.example.sorms_app.data.repository.RoomTypeResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class RoomBookingUiState(
    val isLoading: Boolean = false,
    val rooms: List<RoomData> = emptyList(),
    val roomTypes: List<RoomTypeResponse> = emptyList(),
    val selectedRoomTypeId: Long? = null,
    val errorMessage: String? = null,
    val isBooking: Boolean = false,
    val bookingSuccess: Boolean = false,
    val bookingError: String? = null
)

@HiltViewModel
class RoomBookingViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val roomTypeRepository: RoomTypeRepository,
    private val bookingApiService: BookingApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoomBookingUiState())
    val uiState: StateFlow<RoomBookingUiState> = _uiState.asStateFlow()

    private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    init {
        loadRoomTypes()
        loadRooms()
    }

    private fun loadRoomTypes() {
        viewModelScope.launch {
            when (val result = roomTypeRepository.getAll()) {
                is RoomTypeResult.Success -> {
                    _uiState.update { it.copy(roomTypes = result.data) }
                }
                is RoomTypeResult.Error -> {
                    // Don't fail the whole screen for room types error
                }
            }
        }
    }

    fun loadRooms() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = if (_uiState.value.selectedRoomTypeId != null) {
                roomRepository.getRoomsByType(_uiState.value.selectedRoomTypeId!!)
            } else {
                roomRepository.getAllRooms()
            }

            when (result) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            rooms = result.data
                        ) 
                    }
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = result.message
                        ) 
                    }
                }
                Result.Loading -> {}
            }
        }
    }

    fun selectRoomType(typeId: Long?) {
        _uiState.update { it.copy(selectedRoomTypeId = typeId) }
        loadRooms()
    }

    fun createBooking(
        roomId: Long,
        checkInDate: Date,
        checkOutDate: Date,
        numGuests: Int,
        note: String?
    ) {
        viewModelScope.launch {
            val userId = AuthSession.accountId
            if (userId == null) {
                _uiState.update { it.copy(bookingError = "Bạn cần đăng nhập để đặt phòng") }
                return@launch
            }

            _uiState.update { it.copy(isBooking = true, bookingError = null, bookingSuccess = false) }

            try {
                // Generate booking code
                val timestamp = System.currentTimeMillis()
                val code = "BK-${timestamp.toString().takeLast(8)}"

                val request = CreateBookingRequest(
                    code = code,
                    userId = userId,
                    roomId = roomId,
                    checkinDate = isoDateFormat.format(checkInDate),
                    checkoutDate = isoDateFormat.format(checkOutDate),
                    numGuests = numGuests,
                    note = note
                )

                val response = bookingApiService.createBooking(request)

                if (response.isSuccessful && response.body()?.data != null) {
                    _uiState.update { 
                        it.copy(
                            isBooking = false, 
                            bookingSuccess = true
                        ) 
                    }
                    // Reload rooms to update availability
                    loadRooms()
                } else {
                    val errorBody = response.errorBody()?.string() ?: response.message()
                    _uiState.update { 
                        it.copy(
                            isBooking = false, 
                            bookingError = "Không thể đặt phòng: $errorBody"
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isBooking = false, 
                        bookingError = e.message ?: "Không thể đặt phòng"
                    ) 
                }
            }
        }
    }

    fun resetBookingState() {
        _uiState.update { 
            it.copy(
                bookingSuccess = false, 
                bookingError = null
            ) 
        }
    }

    fun refresh() {
        loadRoomTypes()
        loadRooms()
    }
}

