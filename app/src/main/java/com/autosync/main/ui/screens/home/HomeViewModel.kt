package com.autosync.main.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autosync.main.data.local.dao.VehicleDao
import com.autosync.main.data.local.model.Vehicle
import com.autosync.main.data.repository.User
import com.autosync.main.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val user: User? = null,
    val vehicles: List<Vehicle> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val vehicleDao: VehicleDao,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    init {
        loadCurrentUser()
        loadVehicles()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                val user = userRepository.obtenerUsuario(firebaseUser.uid)
                _state.value = _state.value.copy(user = user, isLoading = false)
            }
        }
    }

    private fun loadVehicles() {
        viewModelScope.launch {
            vehicleDao.getVehicles().collect { vehicles ->
                _state.value = _state.value.copy(vehicles = vehicles)
            }
        }
    }
}
