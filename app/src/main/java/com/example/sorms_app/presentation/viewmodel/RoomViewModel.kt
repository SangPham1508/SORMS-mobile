package com.example.sorms_app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sorms_app.data.datasource.local.AuthSession
import com.example.sorms_app.data.datasource.remote.FaceRecognitionApiService
import com.example.sorms_app.data.models.RoomData
import com.example.sorms_app.domain.repository.BookingRepository
import com.example.sorms_app.data.repository.RoomRepository
import com.example.sorms_app.data.repository.RoomTypeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Trạng thái UI cho màn hình danh sách phòng
 */
data class RoomUiState(
    val isLoading: Boolean = false,
    val rooms: List<RoomData> = emptyList(),
    val filteredRooms: List<RoomData> = emptyList(),
    val selectedFilter: String = "Tất cả",
    val errorMessage: String? = null,
    // Modal state
    val isBookingModalOpen: Boolean = false,
    val selectedRoomForBooking: RoomData? = null,
    // Filters
    val roomTypes: List<com.example.sorms_app.data.models.RoomTypeResponse> = emptyList(),
    val selectedRoomTypeId: Long? = null
)

/**
 * ViewModel quản lý dữ liệu phòng
 */
@HiltViewModel
class RoomViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val roomTypeRepository: RoomTypeRepository,
    private val bookingRepository: BookingRepository,
    private val faceRecognitionApiService: FaceRecognitionApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoomUiState())
    val uiState: StateFlow<RoomUiState> = _uiState.asStateFlow()

    init {
        loadInitial()
    }

    private fun loadInitial() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            // Load room types (ignore errors)
            try {
                val typesResult = roomTypeRepository.getAll()
                if (typesResult is com.example.sorms_app.data.repository.RoomTypeResult.Success) {
                    _uiState.value = _uiState.value.copy(roomTypes = typesResult.data)
                }
            } catch (_: Exception) {
                // ignore
            }
            loadRooms()
        }
    }

    /**
     * Tải danh sách phòng từ API (mặc định)
     */
    fun loadRooms() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            when (val result = roomRepository.getAllRooms()) {
                is com.example.sorms_app.data.repository.Result.Success -> {
                    val allRooms = result.data
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        rooms = allRooms,
                        filteredRooms = filterRooms(allRooms, _uiState.value.selectedFilter),
                        errorMessage = null
                    )
                }
                is com.example.sorms_app.data.repository.Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is com.example.sorms_app.data.repository.Result.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    /**
     * Set filter for rooms
     */
    fun setFilter(filter: String) {
        val currentRooms = _uiState.value.rooms
        _uiState.value = _uiState.value.copy(
            selectedFilter = filter,
            filteredRooms = filterRooms(currentRooms, filter)
        )
    }

    private fun filterRooms(rooms: List<RoomData>, filter: String): List<RoomData> {
        return when (filter) {
            "Khả dụng" -> rooms.filter { it.status.equals("AVAILABLE", ignoreCase = true) }
            "Đang bảo trì" -> rooms.filter { it.status.equals("MAINTENANCE", ignoreCase = true) }
            else -> rooms // "Tất cả"
        }
    }

    /**
     * Lọc theo RoomType
     */
    fun selectRoomType(roomTypeId: Long?) {
        _uiState.value = _uiState.value.copy(selectedRoomTypeId = roomTypeId)
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            if (roomTypeId == null) {
                loadRooms()
                return@launch
            }
            when (val result = roomRepository.getRoomsByType(roomTypeId)) {
                is com.example.sorms_app.data.repository.Result.Success -> {
                    val rooms = result.data
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        rooms = rooms,
                        filteredRooms = filterRooms(rooms, _uiState.value.selectedFilter),
                        errorMessage = null
                    )
                }
                is com.example.sorms_app.data.repository.Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is com.example.sorms_app.data.repository.Result.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    // --- Booking Modal Functions ---

    fun openBookingModal(room: RoomData) {
        _uiState.value = _uiState.value.copy(
            isBookingModalOpen = true,
            selectedRoomForBooking = room
        )
    }

    fun closeBookingModal() {
        _uiState.value = _uiState.value.copy(
            isBookingModalOpen = false,
            selectedRoomForBooking = null
        )
    }

    suspend fun checkFaceStatus(): kotlin.Result<Boolean> {
        return try {
            val userId = AuthSession.accountId ?: return kotlin.Result.failure(Exception("User not logged in"))
            val response = faceRecognitionApiService.getFaceStatus(userId)
            if (response.isSuccessful) {
                kotlin.Result.success(response.body()?.data?.registered ?: false)
            } else {
                kotlin.Result.failure(Exception("API Error: ${response.message()}"))
            }
        } catch (e: Exception) {
            kotlin.Result.failure(e)
        }
    }

    suspend fun createBooking(
        roomId: Long,
        checkInDate: String,
        checkInTime: String,
        checkOutDate: String,
        checkOutTime: String,
        numGuests: Int,
        note: String?
    ): kotlin.Result<Unit> {
        val checkInDateTime = "${checkInDate}T${checkInTime}:00"
        val checkOutDateTime = "${checkOutDate}T${checkOutTime}:00"
        return bookingRepository.createBooking(
            roomId = roomId,
            checkinDate = checkInDateTime,
            checkoutDate = checkOutDateTime,
            numGuests = numGuests,
            note = note
        ).map { Unit }
    }

    /**
     * Làm mới dữ liệu
     */
    fun refresh() {
        selectRoomType(_uiState.value.selectedRoomTypeId)
    }

    /**
     * Xóa thông báo lỗi
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}