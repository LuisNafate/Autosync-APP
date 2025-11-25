package com.autosync.main.ui.screens.addservice

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autosync.main.data.local.model.Service
import com.autosync.main.data.repository.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddServiceViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val serviceType = MutableStateFlow("")
    val workshop = MutableStateFlow("")
    val date = MutableStateFlow<Date?>(null)
    val description = MutableStateFlow("")
    val cost = MutableStateFlow("")

    private val vehicleId: Int = savedStateHandle.get<Int>("vehicleId")!!

    fun onServiceTypeChange(value: String) {
        serviceType.value = value
    }

    fun onWorkshopChange(value: String) {
        workshop.value = value
    }

    fun onDateChange(newDate: Date) {
        date.value = newDate
    }

    fun onDescriptionChange(value: String) {
        description.value = value
    }

    fun onCostChange(value: String) {
        cost.value = value
    }

    fun saveService() {
        viewModelScope.launch {
            val newService = Service(
                vehicleId = vehicleId,
                serviceType = serviceType.value,
                workshop = workshop.value,
                date = date.value ?: Date(),
                description = description.value,
                cost = cost.value.toDoubleOrNull()
            )
            serviceRepository.insertService(newService)
        }
    }
}
