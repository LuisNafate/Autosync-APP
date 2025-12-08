package com.autosync.main.data.repository

import com.autosync.main.data.local.dao.ServiceDao
import com.autosync.main.data.local.model.Service
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.autosync.main.data.local.model.Notification
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

interface ServiceRepository {
    fun getServicesForVehicle(vehicleId: Int): Flow<List<Service>>
    fun getServiceById(serviceId: Int): Flow<Service?>
    suspend fun insertService(service: Service)
    suspend fun updateService(service: Service)
    suspend fun deleteService(service: Service)
    suspend fun syncServices(userId: String) // Added for synchronization
    fun getServicesForUser(userId: String): Flow<List<Service>>
    suspend fun deleteServicesForVehicle(vehicleId: Int)
}

class ServiceRepositoryImpl @Inject constructor(
    private val serviceDao: ServiceDao,
    private val firestore: FirebaseFirestore,
    private val notificationRepository: NotificationRepository
) : ServiceRepository {

    private val serviceCollection = firestore.collection("services")

    override fun getServicesForVehicle(vehicleId: Int): Flow<List<Service>> = serviceDao.getServicesForVehicle(vehicleId)

    override fun getServiceById(serviceId: Int): Flow<Service?> = serviceDao.getServiceById(serviceId)

    override suspend fun insertService(service: Service) {
        val newId = serviceDao.insertService(service)
        val serviceWithId = service.copy(id = newId.toInt())
        try {
            serviceCollection.document(newId.toString()).set(serviceWithId).await()
            
            // Trigger Notification
            if (service.userId.isNotEmpty()) {
                val nextDateMsg = service.nextServiceDate?.let {
                     val fmt = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                     ", recordatorio el día ${fmt.format(it)}"
                } ?: ""
                
                val notification = Notification(
                    userId = service.userId,
                    message = "Nuevo servicio registrado$nextDateMsg",
                    type = "SERVICE",
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
            serviceDao.deleteService(serviceWithId)
            throw e
        }
    }

    override suspend fun updateService(service: Service) {
        serviceCollection.document(service.id.toString()).set(service).await()
        serviceDao.updateService(service)
    }

    override suspend fun deleteService(service: Service) {
        serviceCollection.document(service.id.toString()).delete().await()
        serviceDao.deleteService(service)
    }

    override suspend fun syncServices(userId: String) {
        try {
            val remoteServices = serviceCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()
                .toObjects<Service>()
            
            // This assumes you have a method in your DAO to clear and insert
            // serviceDao.clearUserServices(userId) // Removed to prevent disappearing items due to latency
            serviceDao.insertServices(remoteServices) // You may need to add this method
        } catch (e: Exception) {
            // Handle exceptions
        }
    }


    override fun getServicesForUser(userId: String): Flow<List<Service>> = serviceDao.getServicesForUser(userId)

    override suspend fun deleteServicesForVehicle(vehicleId: Int) {
         // Query Firestore for docs to delete
        val snapshot = serviceCollection.whereEqualTo("vehicleId", vehicleId).get().await()
        if (!snapshot.isEmpty) {
            val batch = firestore.batch()
            for (doc in snapshot.documents) {
                batch.delete(doc.reference)
            }
            batch.commit().await()
        }
        // Delete from Room
        serviceDao.deleteServicesForVehicle(vehicleId)
    }
}
