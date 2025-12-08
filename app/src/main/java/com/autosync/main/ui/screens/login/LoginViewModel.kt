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
    private val serviceRepository: ServiceRepository
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
            vehicleRepository.clearLocalVehicles()
            // You might want to do the same for user and service repositories

            _state.value = _state.value.copy(isLoading = true, generalError = null)
            try {
                val authResult = auth.signInWithEmailAndPassword(_state.value.email, _state.value.password).await()
                val user = authResult.user
                if (user != null) {
                    // Sync all data for the logged-in user
                    userRepository.syncUser(user.uid)
                    vehicleRepository.syncVehicles(user.uid)
                    serviceRepository.syncServices(user.uid) // Added service sync

                    _state.value = _state.value.copy(isLoading = false, isLoginSuccessful = true)
                } else {
                    _state.value = _state.value.copy(isLoading = false, generalError = "Error desconocido durante el login.")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, generalError = e.message)
            }
        }
    }
}
