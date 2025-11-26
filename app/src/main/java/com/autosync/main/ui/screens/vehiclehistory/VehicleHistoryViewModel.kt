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
import java.util.Date
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
            loadVehicle(vehicleId)
            loadServices(vehicleId)
        } else {
            // Si no hay vehicleId, usar datos de ejemplo para desarrollo
            loadMockData()
        }
    }

    private fun loadVehicle(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val vehicleData = vehicleRepository.getVehicleById(id)
                if (vehicleData != null) {
                    _vehicle.value = vehicleData
                } else {
                    // Si no se encuentra el vehículo, usar datos mock
                    loadMockData()
                }
            } catch (e: Exception) {
                // En caso de error, cargar datos de ejemplo
                loadMockData()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadServices(vehicleId: Int) {
        viewModelScope.launch {
            try {
                serviceRepository.getServicesForVehicle(vehicleId).collect {
                    _services.value = it
                }
            } catch (e: Exception) {
                _services.value = emptyList()
            }
        }
    }

    private fun loadMockData() {
        viewModelScope.launch {
            // Datos de ejemplo para desarrollo
            _vehicle.value = Vehicle(
                id = 1,
                make = "Toyota",
                model = "Corolla",
                year = 2020,
                licensePlate = "ABC-123"
            )
            
            _services.value = listOf(
                Service(
                    id = 1,
                    vehicleId = 1,
                    serviceType = "Cambio de Aceite",
                    workshop = "Servicio Express",
                    date = Date(),
                    description = "Filtro y aceite sintético, revisión general.",
                    cost = 800.0
                ),
                Service(
                    id = 2,
                    vehicleId = 1,
                    serviceType = "Rotación y Balanceo de Llantas",
                    workshop = "Llantera del Norte",
                    date = Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000), // 30 días atrás
                    description = "Se ajustó presión y se balancearon 4 ejes de 4 llantas.",
                    cost = 980.0
                )
            )
            
            _isLoading.value = false
        }
    }
}
