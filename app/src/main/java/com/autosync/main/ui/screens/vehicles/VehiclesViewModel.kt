package com.autosync.main.ui.screens.vehicles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autosync.main.data.local.dao.VehicleDao
import com.autosync.main.data.local.model.Vehicle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class VehiclesViewModel @Inject constructor(
    vehicleDao: VehicleDao
) : ViewModel() {

    val vehicles: StateFlow<List<Vehicle>> = vehicleDao.getVehicles()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
