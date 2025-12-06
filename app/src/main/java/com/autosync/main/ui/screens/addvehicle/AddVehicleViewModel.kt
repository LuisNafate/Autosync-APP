package com.autosync.main.ui.screens.addvehicle

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autosync.main.data.local.model.Vehicle
import com.autosync.main.data.remote.nhtsa.dto.ModelDto
import com.autosync.main.data.remote.repository.VehicleApiRepository
import com.autosync.main.data.repository.VehicleRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddVehicleViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val vehicleApiRepository: VehicleApiRepository,
    private val auth: FirebaseAuth, // Inyectamos FirebaseAuth
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val marca = MutableStateFlow("")
    val modelo = MutableStateFlow("")
    val year = MutableStateFlow("")
    val licensePlate = MutableStateFlow("")
    val imageUri = MutableStateFlow<Uri?>(null)

    private val _modelSuggestions = MutableStateFlow<List<ModelDto>>(emptyList())
    val modelSuggestions = _modelSuggestions.asStateFlow()

    private var editingVehicleId: Int? = savedStateHandle.get<Int>("vehicleId")

    init {
        editingVehicleId?.let {
            if (it != -1) { // Hilt/Navigation passes -1 for missing optional args
                loadVehicle(it)
            }
        }
    }

    private fun loadVehicle(id: Int) {
        viewModelScope.launch {
            val vehicle = vehicleRepository.getVehicleById(id)
            vehicle?.let {
                marca.value = it.make
                modelo.value = it.model
                year.value = it.year.toString()
                licensePlate.value = it.licensePlate
                imageUri.value = it.imageUri?.toUri()
            }
        }
    }

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

    fun onImageSelected(uri: Uri) {
        imageUri.value = uri
    }

    fun onModelSelected(model: ModelDto) {
        modelo.value = model.modelName
    }

    private fun searchModels() {
        viewModelScope.launch {
            _modelSuggestions.value = vehicleApiRepository.getModelsForMake(marca.value)
        }
    }

    fun saveVehicle() {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            // Opcional: manejar el error, por ahora simplemente no hacemos nada si no hay usuario
            return
        }

        viewModelScope.launch {
            val vehicle = Vehicle(
                id = editingVehicleId ?: 0,
                make = marca.value,
                model = modelo.value,
                year = year.value.toIntOrNull() ?: 0,
                licensePlate = licensePlate.value,
                imageUri = imageUri.value?.toString(),
                userId = currentUserId // ¡Añadido!
            )
            withContext(Dispatchers.IO) {
                if (editingVehicleId != null && editingVehicleId != -1) {
                    vehicleRepository.updateVehicle(vehicle)
                } else {
                    vehicleRepository.insertVehicle(vehicle)
                }
            }
        }
    }
}
