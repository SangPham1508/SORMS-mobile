package com.example.sorms_app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sorms_app.presentation.screens.user.FaceManagementUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FaceManagementViewModel @Inject constructor(
    // Add face recognition repository when available
) : ViewModel() {

    private val _uiState = MutableStateFlow(FaceManagementUiState())
    val uiState: StateFlow<FaceManagementUiState> = _uiState.asStateFlow()

    fun loadFaceData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                // TODO: Load face data from backend
                // For now, simulate loading
                kotlinx.coroutines.delay(1000)
                
                // Simulate having face data (change to false to test no data state)
                val hasFaceData = true
                
                if (hasFaceData) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            hasFaceData = true,
                            registrationDate = "2024-01-15T10:30:00",
                            lastUpdateDate = "2024-01-15T10:30:00",
                            imageCount = 5, // 3 face + 2 ID card
                            isIdVerified = true,
                            canBookRoom = true
                        ) 
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            hasFaceData = false
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Không thể tải dữ liệu nhận diện"
                    ) 
                }
            }
        }
    }

    fun deleteFaceData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, errorMessage = null) }
            
            try {
                // TODO: Delete face data from backend
                kotlinx.coroutines.delay(2000)
                
                _uiState.update { 
                    it.copy(
                        isDeleting = false,
                        hasFaceData = false,
                        registrationDate = null,
                        lastUpdateDate = null,
                        imageCount = 0,
                        isIdVerified = false,
                        canBookRoom = false
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isDeleting = false,
                        errorMessage = e.message ?: "Không thể xóa dữ liệu nhận diện"
                    ) 
                }
            }
        }
    }

    fun checkFaceRegistrationStatus(): Boolean {
        return _uiState.value.hasFaceData && _uiState.value.isIdVerified
    }

    fun clearState() {
        _uiState.update { FaceManagementUiState() }
    }
}