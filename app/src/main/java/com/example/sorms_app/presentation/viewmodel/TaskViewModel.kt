package com.example.sorms_app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sorms_app.domain.model.Task
import com.example.sorms_app.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskUiState(
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val errorMessage: String? = null,
    val isUpdating: Boolean = false,
    val updateSuccess: Boolean = false,
    val updateError: String? = null,
    // Detail state
    val task: Task? = null
)

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()
    
    // Alias for detail state to match TaskDetailScreen expectation
    val detailUiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    fun loadTasks() {
        viewModelScope.launch {
            taskRepository.getTasks()
                .onStart { 
                    _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                }
                .catch { e -> 
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            errorMessage = e.message ?: "Không thể tải danh sách nhiệm vụ"
                        ) 
                    }
                }
                .collect { tasks ->
                    // Sort by due date ascending (earliest first)
                    val sortedTasks = tasks.sortedBy { it.dueDate }
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            tasks = sortedTasks
                        ) 
                    }
                }
        }
    }

    fun updateTaskStatus(taskId: String, newStatus: String) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isUpdating = true,
                    updateError = null,
                    updateSuccess = false
                ) 
            }
            
            try {
                val status = com.example.sorms_app.domain.model.Status.valueOf(newStatus)
                taskRepository.updateTaskStatus(taskId, status)
                
                // Refresh tasks after update
                loadTasks()
                
                _uiState.update { 
                    it.copy(
                        isUpdating = false,
                        updateSuccess = true
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isUpdating = false,
                        updateError = e.message ?: "Không thể cập nhật trạng thái nhiệm vụ"
                    ) 
                }
            }
        }
    }

    fun loadTaskById(taskId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, task = null) }
            
            try {
                taskRepository.getTaskById(taskId)
                    .collect { task ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                task = task
                            ) 
                        }
                    }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = e.message ?: "Không thể tải chi tiết nhiệm vụ"
                    ) 
                }
            }
        }
    }

    fun refresh() {
        loadTasks()
    }

    fun clearUpdateState() {
        _uiState.update { 
            it.copy(
                updateSuccess = false,
                updateError = null
            ) 
        }
    }
}