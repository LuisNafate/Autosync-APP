package com.autosync.main.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.autosync.main.data.local.UserDatabase
import com.autosync.main.data.repository.User
import com.autosync.main.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeState(
    val user: User? = null,
    val isLoading: Boolean = true
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val userRepository: UserRepository
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    init {
        val userDao = UserDatabase.getDatabase(application).userDao()
        userRepository = UserRepository(userDao)
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                val user = userRepository.obtenerUsuario(firebaseUser.uid)
                _state.value = _state.value.copy(user = user, isLoading = false)
            }
        }
    }
}
