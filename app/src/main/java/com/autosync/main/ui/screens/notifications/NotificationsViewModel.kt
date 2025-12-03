package com.autosync.main.ui.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autosync.main.data.local.model.Notification
import com.autosync.main.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationsState(
    val notifications: List<Notification> = emptyList(),
    val unreadCount: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationsState())
    val state: StateFlow<NotificationsState> = _state.asStateFlow()

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            notificationRepository.getAllNotifications().collect { notifications ->
                _state.value = _state.value.copy(
                    notifications = notifications,
                    isLoading = false
                )
            }
        }
        viewModelScope.launch {
            notificationRepository.getUnreadCount().collect { count ->
                _state.value = _state.value.copy(unreadCount = count)
            }
        }
    }

    fun markAsRead(notificationId: Int) {
        viewModelScope.launch {
            notificationRepository.markAsRead(notificationId)
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            notificationRepository.markAllAsRead()
        }
    }

    fun deleteNotification(notificationId: Int) {
        viewModelScope.launch {
            notificationRepository.deleteNotification(notificationId)
        }
    }
}