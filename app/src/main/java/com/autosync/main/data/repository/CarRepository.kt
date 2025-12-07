package com.autosync.main.data.repository

import com.autosync.main.data.model.Car
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CarRepository {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun addCar(car: Car) {
        firestore.collection("cars").add(car).await()
    }
}
