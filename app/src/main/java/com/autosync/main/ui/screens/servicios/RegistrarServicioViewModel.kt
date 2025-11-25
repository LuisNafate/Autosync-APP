package com.autosync.main.ui.screens.servicios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autosync.main.data.local.dao.VehicleDao
import com.autosync.main.data.local.entities.Servicio
import com.autosync.main.data.local.model.Vehicle
import com.autosync.main.data.repository.ServicioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegistrarServicioState(
    val vehicles: List<Vehicle> = emptyList(),
    val selectedVehicleId: Int? = null,
    val selectedVehicleName: String? = null,
    val tipoServicio: String = "",
    val otroServicio: String = "",
    val categoria: String = "",
    val taller: String = "",
    val fecha: Long = System.currentTimeMillis(),
    val costo: String = "",
    val descripcion: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val tiposServicio: List<String> = listOf(
        "Cambio de aceite",
        "Revisión general",
        "Cambio de frenos",
        "Sistema eléctrico",
        "Aire acondicionado",
        "Cambio de filtros"
    ),
    val categorias: List<String> = listOf(
        "Motor",
        "Frenos",
        "Sistema eléctrico",
        "Aire acondicionado",
        "Transmisión",
        "Suspensión"
    )
) {
    val isValid: Boolean
        get() = selectedVehicleId != null &&
                tipoServicio.isNotBlank() &&
                (tipoServicio != "Otro" || otroServicio.isNotBlank()) &&
                categoria.isNotBlank() &&
                taller.isNotBlank()
}

@HiltViewModel
class RegistrarServicioViewModel @Inject constructor(
    private val servicioRepository: ServicioRepository,
    private val vehicleDao: VehicleDao
) : ViewModel() {

    private val _state = MutableStateFlow(RegistrarServicioState())
    val state: StateFlow<RegistrarServicioState> = _state.asStateFlow()

    init {
        loadVehicles()
    }

    private fun loadVehicles() {
        viewModelScope.launch {
            vehicleDao.getVehicles().collect { vehicles ->
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

    fun onOtroServicioChange(value: String) {
        _state.value = _state.value.copy(otroServicio = value)
    }

    fun onCategoriaChange(categoria: String) {
        _state.value = _state.value.copy(categoria = categoria)
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
                _state.value = _state.value.copy(
                    errorMessage = "Por favor completa todos los campos requeridos"
                )
                return@launch
            }

            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            try {
                val tipoFinal = if (_state.value.tipoServicio == "Otro") {
                    _state.value.otroServicio
                } else {
                    _state.value.tipoServicio
                }

                val servicio = Servicio(
                    vehicleId = _state.value.selectedVehicleId!!,
                    tipoServicio = tipoFinal,
                    categoria = _state.value.categoria,
                    taller = _state.value.taller,
                    fecha = _state.value.fecha,
                    descripcion = _state.value.descripcion,
                    costo = _state.value.costo.toDoubleOrNull() ?: 0.0
                )

                servicioRepository.insertServicio(servicio)
                _state.value = _state.value.copy(isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Error al registrar el servicio: ${e.message}"
                )
            }
        }
    }
}