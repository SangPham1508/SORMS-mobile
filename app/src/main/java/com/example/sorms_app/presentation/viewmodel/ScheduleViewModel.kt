package com.example.sorms_app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sorms_app.domain.model.Task
import com.example.sorms_app.domain.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScheduleUiState(
    val isLoading: Boolean = false,
    val schedule: Map<String, List<Task>> = emptyMap(),
    val errorMessage: String? = null
)

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    init {
        loadSchedule()
    }

    fun loadSchedule() {
        viewModelScope.launch {
            scheduleRepository.getScheduledTasks()
                .onStart { _uiState.value = ScheduleUiState(isLoading = true) }
                .catch { e -> _uiState.value = ScheduleUiState(errorMessage = e.message) }
                .collect { schedule ->
                    _uiState.value = ScheduleUiState(schedule = schedule)
                }
        }
    }
}



