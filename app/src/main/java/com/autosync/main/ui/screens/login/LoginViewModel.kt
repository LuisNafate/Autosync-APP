package com.autosync.main.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginState(
    val email: String = "",
    val password: String = "",
    val recordarme: Boolean = false,
    val isLoading: Boolean = false,
    val isLoginSuccessful: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null
)

class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onEmailChange(email: String) {
        _state.value = _state.value.copy(email = email, emailError = null)
    }

    fun onPasswordChange(password: String) {
        _state.value = _state.value.copy(password = password, passwordError = null)
    }

    fun onRecordarmeChange(recordarme: Boolean) {
        _state.value = _state.value.copy(recordarme = recordarme)
    }

    fun login() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            // Simulate API call
            kotlinx.coroutines.delay(1000)

            // Validations
            val emailError = if (state.value.email.isBlank()) "El email es requerido" else null
            val passwordError = if (state.value.password.isBlank()) "La contraseña es requerida" else null

            val hasErrors = listOf(emailError, passwordError).any { it != null }

            if (!hasErrors) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    isLoginSuccessful = true
                )
            } else {
                _state.value = _state.value.copy(
                    isLoading = false,
                    emailError = emailError,
                    passwordError = passwordError
                )
            }
        }
    }
}