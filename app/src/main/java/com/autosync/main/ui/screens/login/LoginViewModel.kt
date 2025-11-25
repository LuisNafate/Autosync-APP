package com.autosync.main.ui.screens.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.autosync.main.data.local.UserDatabase
import com.autosync.main.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class LoginState(
    val email: String = "",
    val password: String = "",
    val recordarme: Boolean = false,
    val isLoading: Boolean = false,
    val isLoginSuccessful: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val generalError: String? = null,
    val userName: String = ""
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val auth: FirebaseAuth = Firebase.auth
    private val userRepository: UserRepository

    init {
        val userDao = UserDatabase.getDatabase(application).userDao()
        userRepository = UserRepository(userDao)
    }

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
                val result = auth.signInWithEmailAndPassword(state.value.email, state.value.password).await()
                val firebaseUser = result.user
                if (firebaseUser != null) {
                    val user = userRepository.obtenerUsuario(firebaseUser.uid)
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isLoginSuccessful = true,
                        userName = user?.nombre ?: ""
                    )
                } else {
                     _state.value = _state.value.copy(
                        isLoading = false,
                        generalError = "Error al obtener los datos del usuario."
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    generalError = "El email o la contraseña son incorrectos."
                )
            }
        }
    }
}