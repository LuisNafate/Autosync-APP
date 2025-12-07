package com.autosync.main.ui.screens.servicios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autosync.main.data.local.model.Service
import com.autosync.main.data.local.model.Vehicle
import com.autosync.main.data.repository.ServiceRepository
import com.autosync.main.data.repository.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class RegistrarServicioState(
    val vehicles: List<Vehicle> = emptyList(),
    val selectedVehicleId: Int? = null,
    val selectedVehicleName: String? = null,
    val tipoServicio: String = "",
    val otroServicio: String = "", // ¡Añadido!
    val taller: String = "",
    val fecha: Long = System.currentTimeMillis(),
    val costo: String = "",
    val descripcion: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val tiposServicio: List<String> = listOf(
        "Cambio de aceite",
        "Revisión general",
        "Cambio de frenos",
        "Sistema eléctrico",
        "Aire acondicionado",
        "Cambio de filtros"
    )
) {
    val isValid: Boolean
        get() = selectedVehicleId != null &&
                tipoServicio.isNotBlank() &&
                (tipoServicio != "Otro" || otroServicio.isNotBlank()) &&
                taller.isNotBlank()
}

@HiltViewModel
class RegistrarServicioViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository,
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegistrarServicioState())
    val state: StateFlow<RegistrarServicioState> = _state.asStateFlow()

    init {
        loadVehicles()
    }

    private fun loadVehicles() {
        viewModelScope.launch {
            vehicleRepository.getVehicles().collect { vehicles ->
                _state.value = _state.value.copy(vehicles = vehicles)
            }
        }
    }

    fun onVehicleSelected(vehicle: Vehicle) {
        _state.value = _state.value.copy(
            selectedVehicleId = vehicle.id,
            selectedVehicleName = "${vehicle.make} ${vehicle.model} - ${vehicle.licensePlate}"
        )
    }

    fun onTipoServicioChange(tipo: String) {
        _state.value = _state.value.copy(
            tipoServicio = tipo,
            otroServicio = if (tipo != "Otro") "" else _state.value.otroServicio
        )
    }

    fun onOtroServicioChange(value: String) { // ¡Añadido!
        _state.value = _state.value.copy(otroServicio = value)
    }

    fun onTallerChange(taller: String) {
        _state.value = _state.value.copy(taller = taller)
    }

    fun onFechaChange(fecha: Long) {
        _state.value = _state.value.copy(fecha = fecha)
    }

    fun onCostoChange(costo: String) {
        val filtered = costo.filter { it.isDigit() || it == '.' }
        _state.value = _state.value.copy(costo = filtered)
    }

    fun onDescripcionChange(descripcion: String) {
        _state.value = _state.value.copy(descripcion = descripcion)
    }

    fun registrarServicio() {
        viewModelScope.launch {
            if (!_state.value.isValid) {
                _state.value = _state.value.copy(errorMessage = "Por favor completa todos los campos requeridos")
                return@launch
            }

            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            try {
                // Obtener el usuario del vehículo seleccionado
                val selectedVehicle = _state.value.vehicles.find { it.id == _state.value.selectedVehicleId }
                val userId = selectedVehicle?.userId ?: ""

                val service = Service(
                    vehicleId = _state.value.selectedVehicleId!!,
                    userId = userId,
                    serviceType = _state.value.tipoServicio,
                    customService = if (_state.value.tipoServicio == "Otro") _state.value.otroServicio else null,
                    workshop = _state.value.taller,
                    date = Date(_state.value.fecha),
                    details = _state.value.descripcion,
                    cost = _state.value.costo.toDoubleOrNull()
                )

                serviceRepository.insertService(service)
                _state.value = _state.value.copy(isLoading = false, isSuccess = true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Error al registrar el servicio: ${e.message}"
                )
            }
        }
    }
}
