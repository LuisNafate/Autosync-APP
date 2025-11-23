package com.autosync.main.ui.screens.servicios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autosync.main.data.local.dao.VehicleDao
import com.autosync.main.data.local.entities.Servicio
import com.autosync.main.data.local.model.Vehicle
import com.autosync.main.data.repository.ServicioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServiciosViewModel @Inject constructor(
    private val servicioRepository: ServicioRepository,
    private val vehicleDao: VehicleDao
) : ViewModel() {

    val servicios: StateFlow<List<Servicio>> = servicioRepository.getAllServicios()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val vehicles: StateFlow<List<Vehicle>> = vehicleDao.getVehicles()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteServicio(servicio: Servicio) {
        viewModelScope.launch {
            servicioRepository.deleteServicio(servicio)
        }
    }
}