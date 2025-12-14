package com.example.sorms_app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sorms_app.domain.model.Notification
import com.example.sorms_app.domain.repository.ActivityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ActivityUiState(
    val isLoading: Boolean = false,
    val activities: List<Notification> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val activityRepository: ActivityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActivityUiState())
    val uiState: StateFlow<ActivityUiState> = _uiState.asStateFlow()

    init {
        loadActivityHistory()
    }

    fun loadActivityHistory() {
        viewModelScope.launch {
            activityRepository.getActivityHistory()
                .onStart { _uiState.value = ActivityUiState(isLoading = true) }
                .catch { e -> _uiState.value = ActivityUiState(errorMessage = e.message) }
                .collect { activities ->
                    _uiState.value = ActivityUiState(activities = activities)
                }
        }
    }
}



