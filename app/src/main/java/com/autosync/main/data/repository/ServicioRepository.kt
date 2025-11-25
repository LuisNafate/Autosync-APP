package com.autosync.main.data.repository

import com.autosync.main.data.local.dao.ServicioDao
import com.autosync.main.data.local.entities.Servicio
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServicioRepository @Inject constructor(
    private val servicioDao: ServicioDao
) {

    fun getAllServicios(): Flow<List<Servicio>> {
        return servicioDao.getAllServicios()
    }

    fun getServiciosByVehicle(vehicleId: Int): Flow<List<Servicio>> {
        return servicioDao.getServiciosByVehicle(vehicleId)
    }

    suspend fun getServicioById(id: Int): Servicio? {
        return servicioDao.getServicioById(id)
    }

    suspend fun insertServicio(servicio: Servicio) {
        servicioDao.insertServicio(servicio)
    }

    suspend fun updateServicio(servicio: Servicio) {
        servicioDao.updateServicio(servicio)
    }

    suspend fun deleteServicio(servicio: Servicio) {
        servicioDao.deleteServicio(servicio)
    }

    fun getServiciosByDateRange(startDate: Long, endDate: Long): Flow<List<Servicio>> {
        return servicioDao.getServiciosByDateRange(startDate, endDate)
    }
}