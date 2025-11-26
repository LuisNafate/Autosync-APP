package com.autosync.main.data.repository

import com.autosync.main.data.local.dao.ServiceDao
import com.autosync.main.data.local.model.Service
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ServiceRepository {
    fun getServicesForVehicle(vehicleId: Int): Flow<List<Service>>
    fun getServiceById(serviceId: Int): Flow<Service?>
    suspend fun insertService(service: Service)
}

class ServiceRepositoryImpl @Inject constructor(
    private val serviceDao: ServiceDao
) : ServiceRepository {
    override fun getServicesForVehicle(vehicleId: Int): Flow<List<Service>> = serviceDao.getServicesForVehicle(vehicleId)
    override fun getServiceById(serviceId: Int): Flow<Service?> = serviceDao.getServiceById(serviceId)
    override suspend fun insertService(service: Service) = serviceDao.insertService(service)
}
