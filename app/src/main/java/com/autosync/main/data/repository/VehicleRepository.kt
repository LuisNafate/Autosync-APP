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
    suspend fun deleteVehiclesForUser(userId: String)
}

class VehicleRepositoryImpl @Inject constructor(
    private val vehicleDao: VehicleDao,
    private val firestore: FirebaseFirestore,
    private val notificationRepository: NotificationRepository,
    private val serviceRepository: ServiceRepository
) : VehicleRepository {

    private val vehicleCollection = firestore.collection("vehicles")

    override fun getVehicles(): Flow<List<Vehicle>> = vehicleDao.getVehicles()
    override suspend fun getVehicleById(id: Int): Vehicle? = vehicleDao.getVehicleById(id)

    override suspend fun insertVehicle(vehicle: Vehicle) {
        val newId = vehicleDao.insertVehicle(vehicle)
        
        val vehicleWithId = vehicle.copy(id = newId.toInt())
        
        try {
            vehicleCollection.document(newId.toString()).set(vehicleWithId).await()
            
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

                }
            }

        } catch (e: Exception) {
            vehicleDao.deleteVehicle(vehicleWithId)
            throw e
        }
    }

    override suspend fun updateVehicle(vehicle: Vehicle) {
        vehicleCollection.document(vehicle.id.toString()).set(vehicle).await()
        vehicleDao.updateVehicle(vehicle)
    }

    override suspend fun deleteVehicle(vehicle: Vehicle) {
        serviceRepository.deleteServicesForVehicle(vehicle.id)
        
        vehicleCollection.document(vehicle.id.toString()).delete().await()
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

        }
    }

    override suspend fun clearLocalVehicles() {
        vehicleDao.clearAllVehicles()
    }

    override suspend fun deleteVehiclesForUser(userId: String) {
        val vehicles = vehicleCollection.whereEqualTo("userId", userId).get().await().toObjects(Vehicle::class.java)
        
        val batch = firestore.batch()
        vehicles.forEach { vehicle ->
            serviceRepository.deleteServicesForVehicle(vehicle.id)
            batch.delete(vehicleCollection.document(vehicle.id.toString()))
        }
        batch.commit().await()
        
        vehicleDao.deleteUserVehicles(userId)
    }
}
