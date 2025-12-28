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
    object CheckingSession : AuthUiState() // New state for initial session check
    data class Success(val roles: List<String>) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.CheckingSession)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        // Check for existing session when ViewModel is created
        checkExistingSession()
    }

    /**
     * Check if user is already logged in from previous session
     */
    private fun checkExistingSession() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.CheckingSession
            try {
                // Small delay to let UI render first
                kotlinx.coroutines.delay(100)
                when (val result = repository.checkExistingSession()) {
                    is AuthResult.Success -> {
                        _uiState.value = AuthUiState.Success(result.roles)
                    }
                    is AuthResult.Error -> {
                        // No valid session, go to Idle (show login screen)
                        _uiState.value = AuthUiState.Idle
                    }
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Idle
            }
        }
    }

    /**
     * Login với Authorization Code (giống web)
     */
    fun loginWithAuthCode(authCode: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val redirectUri = com.example.sorms_app.BuildConfig.OAUTH_REDIRECT_URI
            when (val result = repository.loginWithAuthCode(authCode, redirectUri)) {
                is AuthResult.Success -> _uiState.value = AuthUiState.Success(result.roles)
                is AuthResult.Error -> _uiState.value = AuthUiState.Error(result.message)
            }
        }
    }

    /**
     * Reset to Idle state (after logout)
     */
    fun reset() {
        _uiState.value = AuthUiState.Idle
    }
}
