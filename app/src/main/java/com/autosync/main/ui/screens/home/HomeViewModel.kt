package com.autosync.main.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.autosync.main.data.local.model.Vehicle
import com.autosync.main.data.repository.VehicleRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val user: com.autosync.main.data.local.model.User? = null,
    val vehicles: List<Vehicle> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            val user = firebaseUser?.let {
                com.autosync.main.data.local.model.User(
                    uid = it.uid,
                    nombre = it.displayName ?: "",
                    email = it.email ?: ""
                )
            }
            _state.value = _state.value.copy(user = user, isLoading = true)

            vehicleRepository.getVehicles().collect {
                _state.value = _state.value.copy(vehicles = it, isLoading = false)
            }
        }
    }
}
