package com.autosync.main.ui.screens.facturas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autosync.main.data.local.model.Service
import com.autosync.main.data.local.model.Vehicle
import com.autosync.main.data.repository.ServiceRepository
import com.autosync.main.data.repository.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class FacturasViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository,
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    fun getServiceById(serviceId: Int): Flow<Service?> {
        return serviceRepository.getServiceById(serviceId)
    }

    fun getVehicleForService(serviceId: Int): Flow<Vehicle?> {
        return serviceRepository.getServiceById(serviceId).map { service ->
            service?.let {
                vehicleRepository.getVehicleById(it.vehicleId)
            }
        }
    }
}