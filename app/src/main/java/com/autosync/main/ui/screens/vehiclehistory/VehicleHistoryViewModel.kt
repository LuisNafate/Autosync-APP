package com.autosync.main.ui.screens.vehiclehistory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autosync.main.data.local.model.Service
import com.autosync.main.data.local.model.Vehicle
import com.autosync.main.data.repository.ServiceRepository
import com.autosync.main.data.repository.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VehicleHistoryViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val serviceRepository: ServiceRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _vehicle = MutableStateFlow<Vehicle?>(null)
    val vehicle = _vehicle.asStateFlow()

    private val _services = MutableStateFlow<List<Service>>(emptyList())
    val services = _services.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        val vehicleId = savedStateHandle.get<Int>("vehicleId")
        
        if (vehicleId != null && vehicleId != -1) {
            loadVehicleAndServices(vehicleId)
        } else {
            _isLoading.value = false
        }
    }

    private fun loadVehicleAndServices(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val vehicleData = vehicleRepository.getVehicleById(id)
                _vehicle.value = vehicleData
            } catch (e: Exception) {
                _vehicle.value = null
            } finally {
                _isLoading.value = false
            }
        }

        viewModelScope.launch {
            try {
                serviceRepository.getServicesForVehicle(id).collect {
                    _services.value = it
                }
            } catch (e: Exception) {
                _services.value = emptyList()
            }
        }
    }
}
