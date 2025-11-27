package com.autosync.main.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class LoginState(
    val email: String = "",
    val password: String = "",
    val recordarme: Boolean = false,
    val isLoading: Boolean = false,
    val isLoginSuccessful: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val generalError: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val auth: FirebaseAuth = Firebase.auth

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
            _state.value = _state.value.copy(isLoading = true, generalError = null)

            // Validations
            val emailError = if (state.value.email.isBlank()) "El email es requerido" else null
            val passwordError = if (state.value.password.isBlank()) "La contraseña es requerida" else null

            if (emailError != null || passwordError != null) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    emailError = emailError,
                    passwordError = passwordError
                )
                return@launch
            }

            try {
                auth.signInWithEmailAndPassword(state.value.email, state.value.password).await()
                _state.value = _state.value.copy(
                    isLoading = false,
                    isLoginSuccessful = true
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    generalError = "El email o la contraseña son incorrectos."
                )
            }
        }
    }
}