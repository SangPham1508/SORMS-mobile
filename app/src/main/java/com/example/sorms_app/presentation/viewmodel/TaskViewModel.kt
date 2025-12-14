package com.example.sorms_app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sorms_app.domain.model.Status
import com.example.sorms_app.domain.model.Task
import com.example.sorms_app.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskListUiState(
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val errorMessage: String? = null
)

data class TaskDetailUiState(
    val isLoading: Boolean = false,
    val task: Task? = null,
    val errorMessage: String? = null,
    val isUpdating: Boolean = false
)

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _listUiState = MutableStateFlow(TaskListUiState())
    val listUiState: StateFlow<TaskListUiState> = _listUiState.asStateFlow()

    private val _detailUiState = MutableStateFlow(TaskDetailUiState())
    val detailUiState: StateFlow<TaskDetailUiState> = _detailUiState.asStateFlow()

    init {
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            taskRepository.getTasks()
                .onStart { _listUiState.value = TaskListUiState(isLoading = true) }
                .catch { e -> _listUiState.value = TaskListUiState(errorMessage = e.message) }
                .collect { tasks ->
                    _listUiState.value = TaskListUiState(tasks = tasks)
                }
        }
    }

    fun loadTaskById(taskId: String) {
        viewModelScope.launch {
            taskRepository.getTaskById(taskId)
                .onStart { _detailUiState.value = TaskDetailUiState(isLoading = true) }
                .catch { e -> _detailUiState.value = TaskDetailUiState(errorMessage = e.message) }
                .collect { task ->
                    _detailUiState.value = TaskDetailUiState(task = task)
                }
        }
    }

    fun updateTaskStatus(taskId: String, status: Status) {
        viewModelScope.launch {
            _detailUiState.value = _detailUiState.value.copy(isUpdating = true)
            try {
                taskRepository.updateTaskStatus(taskId, status)
                // Refresh both detail and list after update
                loadTaskById(taskId)
                loadTasks()
            } catch (e: Exception) {
                _detailUiState.value = _detailUiState.value.copy(errorMessage = e.message)
            } finally {
                _detailUiState.value = _detailUiState.value.copy(isUpdating = false)
            }
        }
    }
}
