package com.autosync.main.ui.screens.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.autosync.main.data.local.UserDatabase
import com.autosync.main.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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
        _state.value = _state.value.copy(email = email, emailError = null, generalError = null)
    }

    fun onPasswordChange(password: String) {
        _state.value = _state.value.copy(password = password, passwordError = null, generalError = null)
    }

    fun onRecordarmeChange(recordarme: Boolean) {
        _state.value = _state.value.copy(recordarme = recordarme)
    }

    fun login() {
        viewModelScope.launch {
            userRepository.clearLocalUser()
            vehicleRepository.clearLocalVehicles()
            serviceRepository.clearLocalServices()
            
            _state.value = _state.value.copy(isLoading = true, generalError = null)
            try {
                val authResult = auth.signInWithEmailAndPassword(_state.value.email, _state.value.password).await()
                val user = authResult.user
                if (user != null) {
                    val sharedPrefs = context.getSharedPreferences("auth_prefs", android.content.Context.MODE_PRIVATE)
                    if (_state.value.recordarme) {
                        val expiryTime = System.currentTimeMillis() + (15L * 60 * 1000)
                        sharedPrefs.edit().putLong("session_expiry", expiryTime).apply()
                    } else {
                        sharedPrefs.edit().remove("session_expiry").apply()
                    }

                    userRepository.syncUser(user.uid)
                    vehicleRepository.syncVehicles(user.uid)
                    serviceRepository.syncServices(user.uid)

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
                    
                    userRepository.guardarUsuario(user.uid, name, email)
                    
                    val sharedPrefs = context.getSharedPreferences("auth_prefs", android.content.Context.MODE_PRIVATE)
                     if (_state.value.recordarme) {
                        val expiryTime = System.currentTimeMillis() + (15L * 60 * 1000) // 15 minutos
                        sharedPrefs.edit().putLong("session_expiry", expiryTime).apply()
                    } else {
                        sharedPrefs.edit().remove("session_expiry").apply()
                    }

                    userRepository.syncUser(user.uid)
                    vehicleRepository.syncVehicles(user.uid)
                    serviceRepository.syncServices(user.uid)

                    _state.value = _state.value.copy(isLoading = false, isLoginSuccessful = true)
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
                    if (_state.value.recordarme) {
                         val expiryTime = System.currentTimeMillis() + (15L * 60 * 1000)
                         sharedPrefs.edit().putLong("session_expiry", expiryTime).apply()
                    } else {
                         sharedPrefs.edit().remove("session_expiry").apply()
                    }
                    
                    userRepository.syncUser(user.uid)
                    vehicleRepository.syncVehicles(user.uid)
                    serviceRepository.syncServices(user.uid)

                    _state.value = _state.value.copy(isLoading = false, isLoginSuccessful = true)
                } else {
                    _state.value = _state.value.copy(isLoading = false, generalError = "Error en Facebook Sign-In.")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, generalError = e.message)
            }
        }
    }
    fun resetPassword(email: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
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
                onResult(false, e.message)
            }
        }
    }
}
