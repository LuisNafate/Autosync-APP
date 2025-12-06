package com.autosync.main.data.repository

import com.autosync.main.data.local.dao.VehicleDao
import com.autosync.main.data.local.model.Vehicle
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface VehicleRepository {
    fun getVehicles(): Flow<List<Vehicle>>
    suspend fun getVehicleById(id: Int): Vehicle?
    suspend fun insertVehicle(vehicle: Vehicle)
    suspend fun updateVehicle(vehicle: Vehicle)
    suspend fun deleteVehicle(vehicle: Vehicle)
}

class VehicleRepositoryImpl @Inject constructor(
    private val vehicleDao: VehicleDao,
    private val firestore: FirebaseFirestore // Inyectamos Firestore
) : VehicleRepository {
    override fun getVehicles(): Flow<List<Vehicle>> = vehicleDao.getVehicles()
    override suspend fun getVehicleById(id: Int): Vehicle? = vehicleDao.getVehicleById(id)

    override suspend fun insertVehicle(vehicle: Vehicle) {
        // Primero, guardamos en Firestore. La colección se crea automáticamente.
        firestore.collection("vehicles").add(vehicle).await()
        // Si lo anterior no lanza una excepción, guardamos en la base de datos local.
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
}
