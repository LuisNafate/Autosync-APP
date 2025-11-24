package com.autosync.main.ui.screens.vehiclehistory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autosync.main.data.local.model.Service
import com.autosync.main.data.local.model.Vehicle
import com.autosync.main.data.repository.VehicleRepository
import com.autosync.main.data.local.dao.ServiceDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VehicleHistoryViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val serviceDao: ServiceDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _vehicle = MutableStateFlow<Vehicle?>(null)
    val vehicle = _vehicle.asStateFlow()

    private val _services = MutableStateFlow<List<Service>>(emptyList())
    val services = _services.asStateFlow()

    init {
        savedStateHandle.get<Int>("vehicleId")?.let {
            if (it != -1) {
                loadVehicle(it)
                loadServices(it)
            }
        }
    }

    private fun loadVehicle(id: Int) {
        viewModelScope.launch {
            _vehicle.value = vehicleRepository.getVehicleById(id)
        }
    }

    private fun loadServices(vehicleId: Int) {
        viewModelScope.launch {
            serviceDao.getServicesForVehicle(vehicleId).collect {
                _services.value = it
            }
        }
    }
}
