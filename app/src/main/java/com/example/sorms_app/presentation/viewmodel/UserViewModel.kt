package com.example.sorms_app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sorms_app.domain.model.User
import com.example.sorms_app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    init {
        loadCurrentUser()
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            userRepository.getCurrentUser()
                .onStart { _uiState.value = UserProfileUiState(isLoading = true) }
                .catch { e -> _uiState.value = UserProfileUiState(errorMessage = e.message) }
                .collect { user ->
                    _uiState.value = UserProfileUiState(user = user)
                }
        }
    }
}
