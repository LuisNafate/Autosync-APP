package com.autosync.main.data.repository

import com.autosync.main.data.local.dao.VehicleDao
import com.autosync.main.data.local.model.Vehicle
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.autosync.main.data.local.model.Notification
import java.util.Date

interface VehicleRepository {
    fun getVehicles(): Flow<List<Vehicle>>
    suspend fun getVehicleById(id: Int): Vehicle?
    suspend fun insertVehicle(vehicle: Vehicle)
    suspend fun updateVehicle(vehicle: Vehicle)
    suspend fun deleteVehicle(vehicle: Vehicle)
    suspend fun syncVehicles(userId: String)
    suspend fun clearLocalVehicles()
}

class VehicleRepositoryImpl @Inject constructor(
    private val vehicleDao: VehicleDao,
    private val firestore: FirebaseFirestore,
    private val notificationRepository: NotificationRepository
) : VehicleRepository {

    private val vehicleCollection = firestore.collection("vehicles")

    override fun getVehicles(): Flow<List<Vehicle>> = vehicleDao.getVehicles()
    override suspend fun getVehicleById(id: Int): Vehicle? = vehicleDao.getVehicleById(id)

    override suspend fun insertVehicle(vehicle: Vehicle) {
        // 1. Insert into Room to get the auto-generated ID
        val newId = vehicleDao.insertVehicle(vehicle)
        
        // 2. Create a copy of the vehicle with the correct ID
        val vehicleWithId = vehicle.copy(id = newId.toInt())
        
        // 3. Save the complete object to Firestore, using the ID as the document key
        try {
            vehicleCollection.document(newId.toString()).set(vehicleWithId).await()
            
            // Trigger Notification
            if (vehicle.userId.isNotEmpty()) {
                val notification = Notification(
                    userId = vehicle.userId,
                    message = "Nuevo vehículo registrado",
                    type = "VEHICLE",
                    relatedId = newId.toString(),
                    date = Date()
                )
                try {
                    notificationRepository.createNotification(notification)
                } catch (e: Exception) {
                    // Ignore notification errors to not block flow
                }
            }

        } catch (e: Exception) {
            // If Firestore fails, roll back the local insert to maintain consistency
            vehicleDao.deleteVehicle(vehicleWithId)
            throw e // Re-throw the exception to notify the caller
        }
    }

    override suspend fun updateVehicle(vehicle: Vehicle) {
        // Update in Firestore first
        vehicleCollection.document(vehicle.id.toString()).set(vehicle).await()
        // Then update in the local database
        vehicleDao.updateVehicle(vehicle)
    }

    override suspend fun deleteVehicle(vehicle: Vehicle) {
        // Delete from Firestore first
        vehicleCollection.document(vehicle.id.toString()).delete().await()
        // Then delete from the local database
        vehicleDao.deleteVehicle(vehicle)
    }

    override suspend fun syncVehicles(userId: String) {
        try {
            val remoteVehicles = vehicleCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()
                .toObjects<Vehicle>()

            vehicleDao.deleteUserVehicles(userId)
            vehicleDao.insertVehicles(remoteVehicles)
        } catch (e: Exception) {
            // Handle network or other errors
        }
    }

    override suspend fun clearLocalVehicles() {
        vehicleDao.clearAllVehicles()
    }
}
