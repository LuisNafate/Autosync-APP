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
    val otroServicio: String = "",
    val taller: String = "",
    val fecha: Long = System.currentTimeMillis(),
    val nextServiceDate: Long? = null,
    val costo: String = "",
    val descripcion: String = "",
    val receiptImageUri: android.net.Uri? = null,
    val existingReceiptImageUrl: String? = null,
    val serviceId: Int? = null,
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

    fun loadServiceForEdit(serviceId: Int) {
        viewModelScope.launch {
            try {
                serviceRepository.getServiceById(serviceId).collect { service ->
                    service?.let {
                        val vehicle = _state.value.vehicles.find { v -> v.id == it.vehicleId }
                        _state.value = _state.value.copy(
                            serviceId = it.id,
                            selectedVehicleId = it.vehicleId,
                            selectedVehicleName = vehicle?.let { v -> "${v.make} ${v.model} - ${v.licensePlate}" },
                            tipoServicio = it.customService ?: it.serviceType,
                            otroServicio = if (it.customService != null) it.customService else "",
                            taller = it.workshop,
                            fecha = it.date.time,
                            nextServiceDate = it.nextServiceDate?.time,
                            costo = it.cost?.toString() ?: "",
                            descripcion = it.details ?: "",
                            existingReceiptImageUrl = it.receiptImageUrl
                        )
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = "Error al cargar el servicio: ${e.message}"
                )
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

    fun onReceiptImageSelected(uri: android.net.Uri?) {
        _state.value = _state.value.copy(receiptImageUri = uri)
    }

    fun onNextServiceDateChange(date: Long?) {
         _state.value = _state.value.copy(nextServiceDate = date)
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
                val selectedVehicle = _state.value.vehicles.find { it.id == _state.value.selectedVehicleId }
                val userId = selectedVehicle?.userId ?: ""

                val isEditMode = _state.value.serviceId != null
                
                if (isEditMode) {
                    // Modo edición - actualizar servicio existente
                    val service = Service(
                        id = _state.value.serviceId!!,
                        vehicleId = _state.value.selectedVehicleId!!,
                        userId = userId,
                        serviceType = _state.value.tipoServicio,
                        customService = if (_state.value.tipoServicio == "Otro") _state.value.otroServicio else null,
                        workshop = _state.value.taller,
                        date = Date(_state.value.fecha),
                        nextServiceDate = _state.value.nextServiceDate?.let { Date(it) },
                        details = _state.value.descripcion,
                        cost = _state.value.costo.toDoubleOrNull(),
                        receiptImageUrl = if (_state.value.receiptImageUri != null) null else _state.value.existingReceiptImageUrl
                    )
                    
                    if (_state.value.receiptImageUri != null) {
                        // Si hay nueva imagen, insertamos con la nueva imagen
                        serviceRepository.insertService(service, _state.value.receiptImageUri)
                    } else {
                        // Si no hay nueva imagen, solo actualizamos
                        serviceRepository.updateService(service)
                    }
                } else {
                    // Modo registro - insertar nuevo servicio
                    val service = Service(
                        vehicleId = _state.value.selectedVehicleId!!,
                        userId = userId,
                        serviceType = _state.value.tipoServicio,
                        customService = if (_state.value.tipoServicio == "Otro") _state.value.otroServicio else null,
                        workshop = _state.value.taller,
                        date = Date(_state.value.fecha),
                        nextServiceDate = _state.value.nextServiceDate?.let { Date(it) },
                        details = _state.value.descripcion,
                        cost = _state.value.costo.toDoubleOrNull()
                    )

                    serviceRepository.insertService(service, _state.value.receiptImageUri)
                }
                
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
