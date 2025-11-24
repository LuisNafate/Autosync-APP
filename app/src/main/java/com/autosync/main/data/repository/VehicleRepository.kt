package com.autosync.main.data.repository

import com.autosync.main.data.local.dao.VehicleDao
import com.autosync.main.data.local.model.Vehicle
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface VehicleRepository {
    fun getVehicles(): Flow<List<Vehicle>>
    suspend fun getVehicleById(id: Int): Vehicle?
    suspend fun insertVehicle(vehicle: Vehicle)
    suspend fun updateVehicle(vehicle: Vehicle)
    suspend fun deleteVehicle(vehicle: Vehicle)
}

class VehicleRepositoryImpl @Inject constructor(
    private val vehicleDao: VehicleDao
) : VehicleRepository {
    override fun getVehicles(): Flow<List<Vehicle>> = vehicleDao.getVehicles()
    override suspend fun getVehicleById(id: Int): Vehicle? = vehicleDao.getVehicleById(id)
    override suspend fun insertVehicle(vehicle: Vehicle) = vehicleDao.insertVehicle(vehicle)
    override suspend fun updateVehicle(vehicle: Vehicle) = vehicleDao.updateVehicle(vehicle)
    override suspend fun deleteVehicle(vehicle: Vehicle) = vehicleDao.deleteVehicle(vehicle)
}
