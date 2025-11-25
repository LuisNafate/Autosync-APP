package com.autosync.main.ui.screens.registro

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
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
    val state = _state.asStateFlow()

    private val auth: FirebaseAuth = Firebase.auth
    private val firestore = Firebase.firestore

    fun onNombreChange(nombre: String) {
        _state.value = _state.value.copy(nombre = nombre, nombreError = null, generalError = null)
    }

    fun onEmailChange(email: String) {
        _state.value = _state.value.copy(email = email, emailError = null, generalError = null)
    }

    fun onPasswordChange(password: String) {
        _state.value = _state.value.copy(password = password, passwordError = null, generalError = null)
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _state.value = _state.value.copy(confirmPassword = confirmPassword, confirmPasswordError = null, generalError = null)
    }

    fun onAceptaTerminosChange(acepta: Boolean) {
        _state.value = _state.value.copy(aceptaTerminos = acepta, terminosError = null, generalError = null)
    }

    private fun validate(): Boolean {
        var isValid = true
        if (_state.value.nombre.isBlank()) {
            _state.value = _state.value.copy(nombreError = "El nombre no puede estar vacío")
            isValid = false
        }
        if (_state.value.password != _state.value.confirmPassword) {
            _state.value = _state.value.copy(confirmPasswordError = "Las contraseñas no coinciden")
            isValid = false
        }
        if (!_state.value.aceptaTerminos) {
            _state.value = _state.value.copy(terminosError = "Debes aceptar los términos y condiciones")
            isValid = false
        }
        return isValid
    }

    fun registrar() {
        if (!validate()) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, generalError = null)
            try {
                val authResult = auth.createUserWithEmailAndPassword(_state.value.email, _state.value.password).await()
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    val user = hashMapOf(
                        "nombre" to _state.value.nombre,
                        "email" to _state.value.email,
                    )
                    firestore.collection("users").document(firebaseUser.uid).set(user).await()
                    _state.value = _state.value.copy(isLoading = false, isRegistroSuccessful = true)
                } else {
                    _state.value = _state.value.copy(isLoading = false, generalError = "No se pudo crear el usuario.")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, generalError = e.message)
            }
        }
    }
}
