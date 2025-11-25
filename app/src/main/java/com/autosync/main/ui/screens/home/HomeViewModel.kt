package com.autosync.main.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autosync.main.data.local.model.User
import com.autosync.main.data.local.model.Vehicle
import com.autosync.main.data.repository.UserRepository
import com.autosync.main.data.repository.VehicleRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val user: User? = null,
    val vehicles: List<Vehicle> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            viewModelScope.launch {
                // Nos suscribimos a los datos del usuario desde la base de datos local (Room)
                userRepository.getUser(currentUser.uid).collect { userFromDb ->
                    _state.value = _state.value.copy(user = userFromDb, isLoading = false)
                }
            }
            viewModelScope.launch {
                // Nos suscribimos a los datos de los vehículos desde la base de datos local (Room)
                vehicleRepository.getVehicles().collect { vehiclesFromDb ->
                    _state.value = _state.value.copy(vehicles = vehiclesFromDb)
                }
            }
        } else {
            // No hay usuario, no hay nada que mostrar
            _state.value = _state.value.copy(isLoading = false)
        }
    }
}
