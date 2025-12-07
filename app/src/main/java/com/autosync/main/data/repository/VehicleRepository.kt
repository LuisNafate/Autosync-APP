package com.autosync.main.data.repository

import com.autosync.main.data.local.dao.VehicleDao
import com.autosync.main.data.local.model.Vehicle
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface VehicleRepository {
    fun getVehicles(): Flow<List<Vehicle>>
    suspend fun getVehicleById(id: Int): Vehicle?
    suspend fun insertVehicle(vehicle: Vehicle)
    suspend fun updateVehicle(vehicle: Vehicle)
    suspend fun deleteVehicle(vehicle: Vehicle)
    suspend fun syncVehicles(userId: String)
    suspend fun clearLocalVehicles() // New method to clear local data
}

class VehicleRepositoryImpl @Inject constructor(
    private val vehicleDao: VehicleDao,
    private val firestore: FirebaseFirestore // Inyectamos Firestore
) : VehicleRepository {
    override fun getVehicles(): Flow<List<Vehicle>> = vehicleDao.getVehicles()
    override suspend fun getVehicleById(id: Int): Vehicle? = vehicleDao.getVehicleById(id)

    override suspend fun insertVehicle(vehicle: Vehicle) {
        firestore.collection("vehicles").add(vehicle).await()
        vehicleDao.insertVehicle(vehicle)
    }

    override suspend fun updateVehicle(vehicle: Vehicle) {
        // TODO: Implementar la lógica de actualización en Firestore
        vehicleDao.updateVehicle(vehicle)
    }

    override suspend fun deleteVehicle(vehicle: Vehicle) {
        // TODO: Implementar la lógica de borrado en Firestore
        vehicleDao.deleteVehicle(vehicle)
    }

    override suspend fun syncVehicles(userId: String) {
        try {
            val remoteVehicles = firestore.collection("vehicles")
                .whereEqualTo("userId", userId)
                .get()
                .await()
                .toObjects<Vehicle>()

            // Clear only the specific user's vehicles before syncing
            vehicleDao.deleteUserVehicles(userId)
            vehicleDao.insertVehicles(remoteVehicles)
        } catch (e: Exception) {
            // Manejar error de red o de otro tipo
        }
    }

    override suspend fun clearLocalVehicles() {
        vehicleDao.clearAllVehicles()
    }
}
