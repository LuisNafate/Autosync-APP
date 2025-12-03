package com.autosync.main.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autosync.main.data.local.model.Vehicle
import com.autosync.main.data.repository.NotificationRepository
import com.autosync.main.data.repository.VehicleRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val user: com.autosync.main.data.local.model.User? = null,
    val vehicles: List<Vehicle> = emptyList(),
    val isLoading: Boolean = true,
    val unreadNotificationsCount: Int = 0
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            val user = firebaseUser?.let {
                com.autosync.main.data.local.model.User(
                    uid = it.uid,
                    nombre = it.displayName ?: "",
                    email = it.email ?: ""
                )
            }
            _state.value = _state.value.copy(user = user, isLoading = true)

            vehicleRepository.getVehicles().collect { vehicles ->
                _state.value = _state.value.copy(vehicles = vehicles, isLoading = false)
            }
        }

        viewModelScope.launch {
            notificationRepository.getUnreadCount().collect { count ->
                _state.value = _state.value.copy(unreadNotificationsCount = count)
            }
        }
    }
}