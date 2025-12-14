package com.example.sorms_app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sorms_app.data.repository.AuthRepository
import com.example.sorms_app.data.repository.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val roles: List<String>) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun loginWithIdToken(idToken: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            when (val result = repository.loginWithGoogleIdToken(idToken)) {
                is AuthResult.Success -> _uiState.value = AuthUiState.Success(result.roles)
                is AuthResult.Error -> _uiState.value = AuthUiState.Error(result.message)
            }
        }
    }

    fun reset() {
        _uiState.value = AuthUiState.Idle
    }
}
