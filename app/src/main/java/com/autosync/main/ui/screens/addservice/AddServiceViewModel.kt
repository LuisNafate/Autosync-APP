package com.autosync.main.ui.screens.addservice

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autosync.main.data.local.model.Service
import com.autosync.main.data.repository.ServiceRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

// New state data class to manage UI state
data class AddServiceState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddServiceViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository,
    private val auth: FirebaseAuth,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val vehicleId: Int = savedStateHandle.get<Int>("vehicleId") ?: -1

    // --- UI State ---
    private val _uiState = MutableStateFlow(AddServiceState())
    val uiState = _uiState.asStateFlow()

    // --- Form Fields State ---
    val serviceType = MutableStateFlow("")
    val customService = MutableStateFlow("")
    val workshop = MutableStateFlow("")
    val date = MutableStateFlow<Date?>(null)
    val description = MutableStateFlow("")
    val cost = MutableStateFlow("")

    // --- Event Handlers ---
    fun onServiceTypeChange(value: String) { serviceType.value = value }
    fun onCustomServiceChange(value: String) { customService.value = value }
    fun onWorkshopChange(value: String) { workshop.value = value }
    fun onDateChange(newDate: Date) { date.value = newDate }
    fun onDescriptionChange(value: String) { description.value = value }
    fun onCostChange(value: String) { cost.value = value }

    fun saveService() {
        viewModelScope.launch {
            _uiState.value = AddServiceState(isLoading = true)

            val isOtro = serviceType.value.equals("Otro", ignoreCase = true)
            
            // serviceType se mantiene como "Otro" si fue seleccionado, el valor real va a customService
            // Si NO es otro, serviceType es el valor seleccionado y customService es null
            
            val currentUserId = auth.currentUser?.uid

            if (serviceType.value.isBlank() || (isOtro && customService.value.isBlank()) || workshop.value.isBlank() || date.value == null || currentUserId == null || vehicleId == -1) {
                _uiState.value = AddServiceState(error = "Por favor, completa todos los campos obligatorios.")
                return@launch
            }

            try {
                val newService = Service(
                    id = 0,
                    vehicleId = vehicleId,
                    userId = currentUserId,
                    serviceType = serviceType.value,
                    customService = if (isOtro) customService.value else null,
                    workshop = workshop.value,
                    date = date.value!!,
                    details = description.value,
                    cost = cost.value.toDoubleOrNull()
                )
                serviceRepository.insertService(newService)
                _uiState.value = AddServiceState(isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = AddServiceState(error = "Error al guardar el servicio: ${e.message}")
            }
        }
    }
}
