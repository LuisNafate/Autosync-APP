package com.autosync.main.ui.screens.servicios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autosync.main.data.local.model.Service
import com.autosync.main.data.local.model.Vehicle
import com.autosync.main.data.repository.ServiceRepository
import com.autosync.main.data.repository.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private var serviceCollectionJobs = mutableListOf<Job>()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            vehicleRepository.getVehicles().collect { vehicles ->

                serviceCollectionJobs.forEach { it.cancel() }
                serviceCollectionJobs.clear()

                _state.value = _state.value.copy(
                    vehicles = vehicles,
                    services = emptyList(),
                    isLoading = true
                )

                if (vehicles.isEmpty()) {
                    _state.value = _state.value.copy(isLoading = false)
                    return@collect
                }

                val allServicesMap = mutableMapOf<Int, List<Service>>()

                vehicles.forEach { vehicle ->
                    val job = launch {
                        serviceRepository.getServicesForVehicle(vehicle.id).collect { services ->
                            allServicesMap[vehicle.id] = services

                            val allServices = allServicesMap.values
                                .flatten()
                                .distinctBy { it.id }
                                .sortedByDescending { it.date }

                            _state.value = _state.value.copy(
                                services = allServices,
                                isLoading = false
                            )
                        }
                    }
                    serviceCollectionJobs.add(job)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        serviceCollectionJobs.forEach { it.cancel() }
    }
}