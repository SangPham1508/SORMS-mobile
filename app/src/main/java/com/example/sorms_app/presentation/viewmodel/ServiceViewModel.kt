package com.example.sorms_app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sorms_app.domain.model.Service
import com.example.sorms_app.domain.repository.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ServiceUiState(
    val isLoading: Boolean = false,
    val services: List<Service> = emptyList(),
    val errorMessage: String? = null,
    val isSubmitting: Boolean = false,
    val submissionSuccess: Boolean = false
)

@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServiceUiState())
    val uiState: StateFlow<ServiceUiState> = _uiState.asStateFlow()

    init {
        loadServices()
    }

    fun loadServices() {
        viewModelScope.launch {
            serviceRepository.getAvailableServices()
                .onStart { _uiState.value = _uiState.value.copy(isLoading = true, submissionSuccess = false, errorMessage = null) }
                .catch { e -> _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message) }
                .collect { services ->
                    _uiState.value = _uiState.value.copy(isLoading = false, services = services)
                }
        }
    }

    fun createServiceRequest(serviceId: String, notes: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, errorMessage = null, submissionSuccess = false)
            try {
                serviceRepository.createServiceRequest(serviceId, notes)
                _uiState.value = _uiState.value.copy(isSubmitting = false, submissionSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSubmitting = false, errorMessage = e.message)
            }
        }
    }
    
    fun resetSubmissionState() {
        _uiState.value = _uiState.value.copy(isSubmitting = false, submissionSuccess = false, errorMessage = null)
    }
}
