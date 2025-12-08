package com.autosync.main.ui.screens.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autosync.main.data.repository.UserRepository
import com.autosync.main.data.repository.VehicleRepository
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
    private val userRepository: UserRepository,
    private val vehicleRepository: VehicleRepository,
    private val serviceRepository: com.autosync.main.data.repository.ServiceRepository,
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context
) : ViewModel() {

    private val _state = MutableStateFlow(RegistroState())
    val state = _state.asStateFlow()

    private val auth: FirebaseAuth = Firebase.auth

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
            // ** THE FIX: Clear local data before starting a new session **
            userRepository.clearLocalUser()
            vehicleRepository.clearLocalVehicles()
            serviceRepository.clearLocalServices()

            _state.value = _state.value.copy(isLoading = true, generalError = null)
            try {
                val authResult = auth.createUserWithEmailAndPassword(_state.value.email, _state.value.password).await()
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    userRepository.guardarUsuario(firebaseUser.uid, _state.value.nombre, _state.value.email)
                    _state.value = _state.value.copy(isLoading = false, isRegistroSuccessful = true)
                } else {
                    _state.value = _state.value.copy(isLoading = false, generalError = "No se pudo crear el usuario.")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, generalError = e.message)
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            userRepository.clearLocalUser()
            vehicleRepository.clearLocalVehicles()
            serviceRepository.clearLocalServices()

            _state.value = _state.value.copy(isLoading = true, generalError = null)
            try {
                val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
                val authResult = auth.signInWithCredential(credential).await()
                val user = authResult.user
                
                if (user != null) {
                    val name = user.displayName ?: "Usuario Google"
                    val email = user.email ?: ""
                    
                    // Asegurar que exista en Firestore (si es nuevo registro via Google)
                    userRepository.guardarUsuario(user.uid, name, email)
                    
                    val sharedPrefs = context.getSharedPreferences("auth_prefs", android.content.Context.MODE_PRIVATE)
                    // En registro asumimos sesión normal, o podríamos no setear expiry. 
                    // Para consistencia con login, lo dejamos sin "recordarme" explicito o default.
                    // Pero el usuario viene de registrarse, asi que logueamos.
                    sharedPrefs.edit().remove("session_expiry").apply() 

                    userRepository.syncUser(user.uid)
                    vehicleRepository.syncVehicles(user.uid)
                    serviceRepository.syncServices(user.uid)

                    _state.value = _state.value.copy(isLoading = false, isRegistroSuccessful = true)
                } else {
                    _state.value = _state.value.copy(isLoading = false, generalError = "Error en Google Sign-In.")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, generalError = e.message)
            }
        }
    }

    fun signInWithFacebook(accessToken: com.facebook.AccessToken) {
        viewModelScope.launch {
            userRepository.clearLocalUser()
            vehicleRepository.clearLocalVehicles()
            serviceRepository.clearLocalServices()

            _state.value = _state.value.copy(isLoading = true, generalError = null)
            try {
                val credential = com.google.firebase.auth.FacebookAuthProvider.getCredential(accessToken.token)
                val authResult = auth.signInWithCredential(credential).await()
                val user = authResult.user

                if (user != null) {
                    val name = user.displayName ?: "Usuario Facebook"
                    val email = user.email ?: ""

                    userRepository.guardarUsuario(user.uid, name, email)
                    
                    val sharedPrefs = context.getSharedPreferences("auth_prefs", android.content.Context.MODE_PRIVATE)
                    sharedPrefs.edit().remove("session_expiry").apply()
                    
                    userRepository.syncUser(user.uid)
                    vehicleRepository.syncVehicles(user.uid)
                    serviceRepository.syncServices(user.uid)

                    _state.value = _state.value.copy(isLoading = false, isRegistroSuccessful = true)
                } else {
                    _state.value = _state.value.copy(isLoading = false, generalError = "Error en Facebook Sign-In.")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, generalError = e.message)
            }
        }
    }
}
