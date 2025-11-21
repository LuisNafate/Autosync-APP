package com.autosync.main.ui.screens.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autosync.main.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class RegistroState(
    val nombre: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val aceptaTerminos: Boolean = false,
    val isLoading: Boolean = false,
    val isRegistroSuccessful: Boolean = false,
    val nombreError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val terminosError: String? = null,
    val generalError: String? = null
)

class RegistroViewModel : ViewModel() {
    private val _state = MutableStateFlow(RegistroState())
    val state: StateFlow<RegistroState> = _state.asStateFlow()

    private val auth: FirebaseAuth = Firebase.auth
    private val userRepository = UserRepository()

    fun onNombreChange(nombre: String) {
        _state.value = _state.value.copy(nombre = nombre, nombreError = null)
    }

    fun onEmailChange(email: String) {
        _state.value = _state.value.copy(email = email, emailError = null)
    }

    fun onPasswordChange(password: String) {
        _state.value = _state.value.copy(password = password, passwordError = null)
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _state.value = _state.value.copy(confirmPassword = confirmPassword, confirmPasswordError = null)
    }

    fun onAceptaTerminosChange(acepta: Boolean) {
        _state.value = _state.value.copy(aceptaTerminos = acepta, terminosError = null)
    }

    fun registrar() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, generalError = null)

            // Validaciones
            val nombreError = if (_state.value.nombre.isBlank()) "El nombre es requerido" else null
            val emailError = if (_state.value.email.isBlank()) "El email es requerido" else null
            val passwordError = if (_state.value.password.isBlank()) "La contraseña es requerida" else null
            val confirmPasswordError = if (_state.value.password != _state.value.confirmPassword) "Las contraseñas no coinciden" else null
            val terminosError = if (!_state.value.aceptaTerminos) "Debes aceptar los términos y condiciones" else null

            val hasErrors = listOf(nombreError, emailError, passwordError, confirmPasswordError, terminosError).any { it != null }

            if (hasErrors) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    nombreError = nombreError,
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError,
                    terminosError = terminosError
                )
                return@launch
            }

            try {
                val result = auth.createUserWithEmailAndPassword(_state.value.email, _state.value.password).await()
                val firebaseUser = result.user
                if (firebaseUser != null) {
                    userRepository.guardarUsuario(
                        uid = firebaseUser.uid,
                        nombre = _state.value.nombre,
                        email = _state.value.email
                    )
                }

                _state.value = _state.value.copy(
                    isLoading = false,
                    isRegistroSuccessful = true
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    generalError = e.message ?: "Ocurrió un error inesperado"
                )
            }
        }
    }
}