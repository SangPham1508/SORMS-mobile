package com.example.sorms_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sorms_app.data.model.RoomData
import com.example.sorms_app.data.repository.Result
import com.example.sorms_app.data.repository.RoomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Trạng thái UI cho màn hình danh sách phòng
 */
data class RoomUiState(
    val isLoading: Boolean = false,
    val rooms: List<RoomData> = emptyList(),
    val availableRooms: List<RoomData> = emptyList(),
    val occupiedRooms: List<RoomData> = emptyList(),
    val errorMessage: String? = null,
    val selectedRoomNumber: String? = null
)

/**
 * ViewModel quản lý dữ liệu phòng
 */
class RoomViewModel : ViewModel() {
    
    private val repository = RoomRepository()
    
    private val _uiState = MutableStateFlow(RoomUiState())
    val uiState: StateFlow<RoomUiState> = _uiState.asStateFlow()
    
    init {
        loadRooms()
    }
    
    /**
     * Tải danh sách phòng từ API
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
                        availableRooms = allRooms.filter { it.isAvailable },
                        occupiedRooms = allRooms.filter { !it.isAvailable },
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
        loadRooms()
    }
    
    /**
     * Xóa thông báo lỗi
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}



