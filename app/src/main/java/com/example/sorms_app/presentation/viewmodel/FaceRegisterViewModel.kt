package com.example.sorms_app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sorms_app.presentation.screens.user.FaceRegisterUiState
import com.example.sorms_app.presentation.screens.user.FaceRegisterStep
import com.example.sorms_app.presentation.screens.user.CaptureType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FaceRegisterViewModel @Inject constructor(
    // Add face recognition repository when available
) : ViewModel() {

    private val _uiState = MutableStateFlow(FaceRegisterUiState())
    val uiState: StateFlow<FaceRegisterUiState> = _uiState.asStateFlow()

    fun nextStep() {
        val currentStep = _uiState.value.currentStep
        val nextStep = when (currentStep) {
            FaceRegisterStep.INTRODUCTION -> FaceRegisterStep.CAMERA_PERMISSION
            FaceRegisterStep.CAMERA_PERMISSION -> FaceRegisterStep.FACE_LEFT
            FaceRegisterStep.FACE_LEFT -> FaceRegisterStep.FACE_RIGHT
            FaceRegisterStep.FACE_RIGHT -> FaceRegisterStep.FACE_FRONT
            FaceRegisterStep.FACE_FRONT -> FaceRegisterStep.ID_CARD_FRONT
            FaceRegisterStep.ID_CARD_FRONT -> FaceRegisterStep.ID_CARD_BACK
            FaceRegisterStep.ID_CARD_BACK -> FaceRegisterStep.REVIEW
            FaceRegisterStep.REVIEW -> FaceRegisterStep.PROCESSING
            FaceRegisterStep.PROCESSING -> return // Stay at processing
        }
        
        _uiState.update { it.copy(currentStep = nextStep, errorMessage = null) }
    }

    fun previousStep() {
        val currentStep = _uiState.value.currentStep
        val previousStep = when (currentStep) {
            FaceRegisterStep.INTRODUCTION -> return // Can't go back from first step
            FaceRegisterStep.CAMERA_PERMISSION -> FaceRegisterStep.INTRODUCTION
            FaceRegisterStep.FACE_LEFT -> FaceRegisterStep.CAMERA_PERMISSION
            FaceRegisterStep.FACE_RIGHT -> FaceRegisterStep.FACE_LEFT
            FaceRegisterStep.FACE_FRONT -> FaceRegisterStep.FACE_RIGHT
            FaceRegisterStep.ID_CARD_FRONT -> FaceRegisterStep.FACE_FRONT
            FaceRegisterStep.ID_CARD_BACK -> FaceRegisterStep.ID_CARD_FRONT
            FaceRegisterStep.REVIEW -> FaceRegisterStep.ID_CARD_BACK
            FaceRegisterStep.PROCESSING -> FaceRegisterStep.REVIEW
        }
        
        _uiState.update { it.copy(currentStep = previousStep, errorMessage = null) }
    }

    fun captureImage(captureType: CaptureType) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isCapturing = true,
                    errorMessage = null
                ) 
            }
            
            try {
                // TODO: Implement actual image capture using camera
                // For now, simulate capture process
                kotlinx.coroutines.delay(2000)
                
                // Simulate success and store image path
                val currentImages = _uiState.value.capturedImages.toMutableMap()
                currentImages[captureType] = "captured_image_${captureType.name}.jpg"
                
                _uiState.update { 
                    it.copy(
                        isCapturing = false,
                        capturedImages = currentImages
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isCapturing = false,
                        errorMessage = e.message ?: "Không thể chụp ảnh"
                    ) 
                }
            }
        }
    }

    fun retryCapture() {
        _uiState.update { 
            it.copy(
                errorMessage = null
            ) 
        }
    }

    fun retakeImage(captureType: CaptureType) {
        // Remove the captured image and go back to that step
        val currentImages = _uiState.value.capturedImages.toMutableMap()
        currentImages.remove(captureType)
        
        val targetStep = when (captureType) {
            CaptureType.FACE_LEFT -> FaceRegisterStep.FACE_LEFT
            CaptureType.FACE_RIGHT -> FaceRegisterStep.FACE_RIGHT
            CaptureType.FACE_FRONT -> FaceRegisterStep.FACE_FRONT
            CaptureType.ID_CARD_FRONT -> FaceRegisterStep.ID_CARD_FRONT
            CaptureType.ID_CARD_BACK -> FaceRegisterStep.ID_CARD_BACK
        }
        
        _uiState.update { 
            it.copy(
                currentStep = targetStep,
                capturedImages = currentImages,
                errorMessage = null
            ) 
        }
    }

    fun completeFaceRegistration() {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isRegistering = true,
                    errorMessage = null
                ) 
            }
            
            try {
                // TODO: Send all captured images to backend for registration
                // Process face recognition data and ID verification
                kotlinx.coroutines.delay(3000)
                
                _uiState.update { 
                    it.copy(
                        isRegistering = false,
                        registrationSuccess = true
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isRegistering = false,
                        errorMessage = e.message ?: "Không thể hoàn thành đăng ký"
                    ) 
                }
            }
        }
    }

    fun clearState() {
        _uiState.update { FaceRegisterUiState() }
    }
}