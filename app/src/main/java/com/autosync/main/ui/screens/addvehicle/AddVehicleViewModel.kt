package com.autosync.main.ui.screens.addvehicle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autosync.main.data.local.dao.VehicleDao
import com.autosync.main.data.local.model.Vehicle
import com.autosync.main.data.remote.dto.CarDto
import com.autosync.main.data.remote.repository.VehicleApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddVehicleViewModel @Inject constructor(
    private val vehicleDao: VehicleDao,
    private val vehicleApiRepository: VehicleApiRepository
) : ViewModel() {

    val marca = MutableStateFlow("")
    val modelo = MutableStateFlow("")
    val year = MutableStateFlow("")
    val licensePlate = MutableStateFlow("")

    private val _carSuggestions = MutableStateFlow<List<CarDto>>(emptyList())
    val carSuggestions = _carSuggestions.asStateFlow()

    fun onMarcaChange(value: String) {
        marca.value = value
        if (value.length > 2) { // To avoid too many API calls
            searchModels()
        }
    }

    fun onModeloChange(value: String) {
        modelo.value = value
    }

    fun onYearChange(value: String) {
        year.value = value
    }

    fun onLicensePlateChange(value: String) {
        licensePlate.value = value
    }

    fun onCarSelected(car: CarDto) {
        modelo.value = car.model
        year.value = car.year.toString()
    }

    private fun searchModels() {
        viewModelScope.launch {
            _carSuggestions.value = vehicleApiRepository.getCarsByMake(marca.value)
        }
    }

    fun saveVehicle() {
        viewModelScope.launch {
            val vehicle = Vehicle(
                make = marca.value,
                model = modelo.value,
                year = year.value.toIntOrNull() ?: 0,
                licensePlate = licensePlate.value
            )
            withContext(Dispatchers.IO) {
                vehicleDao.insertVehicle(vehicle)
            }
        }
    }
}
