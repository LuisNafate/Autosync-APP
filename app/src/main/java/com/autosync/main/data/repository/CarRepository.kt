package com.autosync.main.data.repository

import com.autosync.main.data.model.Car
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

// Convert to a singleton object to ensure a single instance throughout the app
object CarRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val carsCollection = firestore.collection("cars")

    // StateFlow to hold the list of cars for the current user
    private val _carsFlow = MutableStateFlow<List<Car>>(emptyList())
    val carsFlow = _carsFlow.asStateFlow()

    suspend fun addCar(car: Car) {
        carsCollection.add(car).await()
        // Refresh the list after adding a new car
        getCarsByUserId(car.userId)
    }

    suspend fun getCarsByUserId(userId: String) {
        try {
            val snapshot = carsCollection.whereEqualTo("userId", userId).get().await()
            _carsFlow.value = snapshot.toObjects<Car>()
        } catch (e: Exception) {
            // Handle error, maybe log it or emit an error state
            _carsFlow.value = emptyList()
        }
    }

    /**
     * Clears the local car data. This should be called on user sign-out
     * or before a new user signs in to prevent state bleeding.
     */
    fun clearCarsOnSignOut() {
        _carsFlow.value = emptyList()
    }
}
