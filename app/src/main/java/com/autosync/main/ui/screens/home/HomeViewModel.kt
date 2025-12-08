package com.autosync.main.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autosync.main.data.local.UserEntity
import com.autosync.main.data.local.model.Vehicle
import com.autosync.main.data.repository.UserRepository
import com.autosync.main.data.repository.VehicleRepository
import com.autosync.main.data.repository.NotificationRepository
import com.autosync.main.data.local.model.Notification
import com.autosync.main.data.local.model.Service
import com.autosync.main.data.repository.ServiceRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val user: UserEntity? = null,
    val documents: List<Any> = emptyList(), // Placeholder for now
    val vehicles: List<Vehicle> = emptyList(),
    val recentServices: List<Service> = emptyList(),
    val notifications: List<Notification> = emptyList(),
    val unreadCount: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val vehicleRepository: VehicleRepository,
    private val notificationRepository: NotificationRepository,
    private val serviceRepository: ServiceRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            firebaseUser?.let { user ->
                userRepository.obtenerUsuario(user.uid).collect { userEntity ->
                    _state.value = _state.value.copy(user = userEntity)
                }
            }

            vehicleRepository.getVehicles().collect {
                _state.value = _state.value.copy(vehicles = it, isLoading = false)
            }
        }
        
        viewModelScope.launch {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
             firebaseUser?.uid?.let { uid ->
                notificationRepository.getUserNotifications(uid).collect { notifications ->
                    val unread = notifications.count { !it.read }
                    _state.value = _state.value.copy(
                        notifications = notifications,
                        unreadCount = unread
                    )
                }
            }
        }

        viewModelScope.launch {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            firebaseUser?.uid?.let { uid ->
                serviceRepository.getServicesForUser(uid).collect { services ->
                    _state.value = _state.value.copy(
                        recentServices = services
                    )
                }
            }
        }
    }
    
    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            notificationRepository.markAsRead(notificationId)
        }
    }
}
