package com.example.sorms_app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sorms_app.data.models.RoomData
import com.example.sorms_app.data.repository.Result
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
    val selectedRoomNumber: String? = null,
    // Filters
    val roomTypes: List<com.example.sorms_app.data.models.RoomTypeResponse> = emptyList(),
    val selectedRoomTypeId: Long? = null
)

/**
 * ViewModel quản lý dữ liệu phòng
 */
@HiltViewModel
class RoomViewModel @Inject constructor(
    private val repository: RoomRepository,
    private val roomTypeRepository: RoomTypeRepository
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
            when (val types = roomTypeRepository.getAll()) {
                is com.example.sorms_app.data.repository.RoomTypeResult.Success -> {
                    _uiState.value = _uiState.value.copy(roomTypes = types.data)
                }
                else -> { /* ignore */ }
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

            when (val result = repository.getAllRooms()) {
                is Result.Success -> {
                    val allRooms = result.data
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        rooms = allRooms,
                        filteredRooms = filterRooms(allRooms, _uiState.value.selectedFilter),
                        errorMessage = null
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is Result.Loading -> {
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
            when (val result = repository.getRoomsByType(roomTypeId)) {
                is Result.Success -> {
                    val rooms = result.data
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        rooms = rooms,
                        filteredRooms = filterRooms(rooms, _uiState.value.selectedFilter),
                        errorMessage = null
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is Result.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    /**
     * Chọn/bỏ chọn phòng
     */
    fun selectRoom(roomNumber: String?) {
        val currentSelected = _uiState.value.selectedRoomNumber
        _uiState.value = _uiState.value.copy(
            selectedRoomNumber = if (currentSelected == roomNumber) null else roomNumber
        )
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


