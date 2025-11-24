package com.autosync.main.ui.screens.vehicles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autosync.main.data.local.model.Vehicle
import com.autosync.main.data.repository.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VehiclesViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    val vehicles: StateFlow<List<Vehicle>> = vehicleRepository.getVehicles()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteVehicle(vehicle: Vehicle) {
        viewModelScope.launch {
            vehicleRepository.deleteVehicle(vehicle)
        }
    }
}
