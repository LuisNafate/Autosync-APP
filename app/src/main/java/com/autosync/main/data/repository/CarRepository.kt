package com.autosync.main.data.repository

import com.autosync.main.data.model.Car
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

object CarRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val carsCollection = firestore.collection("cars")

    private val _carsFlow = MutableStateFlow<List<Car>>(emptyList())
    val carsFlow = _carsFlow.asStateFlow()

    suspend fun addCar(car: Car) {
        carsCollection.add(car).await()
        getCarsByUserId(car.userId)
    }

    suspend fun getCarsByUserId(userId: String) {
        try {
            val snapshot = carsCollection.whereEqualTo("userId", userId).get().await()
            _carsFlow.value = snapshot.toObjects<Car>()
        } catch (e: Exception) {
            _carsFlow.value = emptyList()
        }
    }


    fun clearCarsOnSignOut() {
        _carsFlow.value = emptyList()
    }
}
