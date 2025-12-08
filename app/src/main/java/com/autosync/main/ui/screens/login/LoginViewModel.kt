package com.autosync.main.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autosync.main.data.repository.ServiceRepository
import com.autosync.main.data.repository.UserRepository
import com.autosync.main.data.repository.VehicleRepository
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
    val generalError: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val vehicleRepository: VehicleRepository,
    private val serviceRepository: ServiceRepository,
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

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

                    _state.value = _state.value.copy(isLoading = false, isLoginSuccessful = true)
                } else {
                    _state.value = _state.value.copy(isLoading = false, generalError = "Error desconocido durante el login.")
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
                auth.sendPasswordResetEmail(email).await()
                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.message)
            }
        }
    }
}
