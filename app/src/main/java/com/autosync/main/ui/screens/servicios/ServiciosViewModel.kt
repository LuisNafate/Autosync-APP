package com.autosync.main.ui.screens.servicios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autosync.main.data.local.model.Service
import com.autosync.main.data.local.model.Vehicle
import com.autosync.main.data.repository.ServiceRepository
import com.autosync.main.data.repository.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ServiciosState(
    val services: List<Service> = emptyList(),
    val vehicles: List<Vehicle> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class ServiciosViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository,
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ServiciosState())
    val state: StateFlow<ServiciosState> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            viewModelScope.launch {
                // Sincronizar servicios remotos
                try {
                    serviceRepository.syncServices(userId)
                } catch (e: Exception) {
                    // Manejar error de sync silenciosamente o mostrar en UI
                }
            }

            viewModelScope.launch {
                combine(
                    vehicleRepository.getVehicles(),
                    serviceRepository.getServicesForUser(userId)
                ) { vehicles, services ->
                    ServiciosState(
                        vehicles = vehicles,
                        services = services,
                        isLoading = false
                    )
                }.collect { newState ->
                    _state.value = newState
                }
            }
        } else {
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    // La lógica de borrado necesitará ser repensada, 
    // pero por ahora la comentamos para que no de errores de compilación.
    /*
    fun deleteServicio(service: Service) {
        viewModelScope.launch {
            serviceRepository.deleteService(service) // Suponiendo que exista un método deleteService
        }
    }
    */
}
