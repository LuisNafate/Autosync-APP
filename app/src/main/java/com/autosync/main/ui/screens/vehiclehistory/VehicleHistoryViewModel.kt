package com.autosync.main.ui.screens.vehiclehistory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autosync.main.data.local.dao.VehicleDao
import com.autosync.main.data.local.model.Vehicle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VehicleHistoryViewModel @Inject constructor(
    private val vehicleDao: VehicleDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _vehicle = MutableStateFlow<Vehicle?>(null)
    val vehicle = _vehicle.asStateFlow()

    init {
        savedStateHandle.get<Int>("vehicleId")?.let {
            loadVehicle(it)
        }
    }

    private fun loadVehicle(id: Int) {
        viewModelScope.launch {
            _vehicle.value = vehicleDao.getVehicleById(id)
        }
    }
}
