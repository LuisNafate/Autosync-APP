package com.autosync.main.ui.screens.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
<<<<<<< Updated upstream
import com.autosync.main.data.repository.UserRepository
=======
import com.autosync.main.data.model.Car
import com.autosync.main.data.repository.CarRepository
>>>>>>> Stashed changes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

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

@HiltViewModel
class RegistroViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegistroState())
    val state = _state.asStateFlow()

    private val auth: FirebaseAuth = Firebase.auth
<<<<<<< Updated upstream
=======
    private val firestore = Firebase.firestore
    private val carRepository = CarRepository()
>>>>>>> Stashed changes

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
<<<<<<< Updated upstream
                    userRepository.guardarUsuario(firebaseUser.uid, _state.value.nombre, _state.value.email)
=======
                    val user = hashMapOf(
                        "nombre" to _state.value.nombre,
                        "email" to _state.value.email,
                    )
                    firestore.collection("users").document(firebaseUser.uid).set(user).await()

                    // Add a sample car for the new user
                    val sampleCar = Car(
                        userId = firebaseUser.uid,
                        brand = "Toyota",
                        model = "Corolla",
                        plate = "AUT0123",
                        year = 2023
                    )
                    carRepository.addCar(sampleCar)

>>>>>>> Stashed changes
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
