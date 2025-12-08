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
    suspend fun insertService(service: Service, imageUri: android.net.Uri? = null)
    suspend fun updateService(service: Service)
    suspend fun deleteService(service: Service)
    suspend fun syncServices(userId: String)
    fun getServicesForUser(userId: String): Flow<List<Service>>
    suspend fun deleteServicesForVehicle(vehicleId: Int)
    suspend fun clearLocalServices()
}

class ServiceRepositoryImpl @Inject constructor(
    private val serviceDao: ServiceDao,
    private val firestore: FirebaseFirestore,
    private val notificationRepository: NotificationRepository,
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context
) : ServiceRepository {

    private val serviceCollection = firestore.collection("services")


    override fun getServicesForVehicle(vehicleId: Int): Flow<List<Service>> = serviceDao.getServicesForVehicle(vehicleId)

    override fun getServiceById(serviceId: Int): Flow<Service?> = serviceDao.getServiceById(serviceId)



    override suspend fun insertService(service: Service, imageUri: android.net.Uri?) {
        val newId = serviceDao.insertService(service)
        var serviceWithId = service.copy(id = newId.toInt())

        try {
            if (imageUri != null) {
                val base64Image = compressUriToBase64(imageUri)
                if (base64Image != null) {
                    serviceWithId = serviceWithId.copy(receiptImageUrl = base64Image)
                    serviceDao.updateService(serviceWithId)
                }
            }


            serviceCollection.document(newId.toString()).set(serviceWithId).await()
            
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
                }
            }

        } catch (e: Exception) {
            throw e
        }
    }

    private suspend fun compressUriToBase64(uri: android.net.Uri): String? = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            
            if (bitmap == null) return@withContext null

            val maxDimension = 800
            val ratio = Math.min(
                maxDimension.toFloat() / bitmap.width,
                maxDimension.toFloat() / bitmap.height
            )
            val width = (bitmap.width * ratio).toInt()
            val height = (bitmap.height * ratio).toInt()
            
            val resizedBitmap = android.graphics.Bitmap.createScaledBitmap(bitmap, width, height, true)

            val outputStream = java.io.ByteArrayOutputStream()
            resizedBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 60, outputStream)
            val byteArray = outputStream.toByteArray()
            
            android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            null
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
            

            serviceDao.insertServices(remoteServices)
        } catch (e: Exception) {

        }
    }


    override fun getServicesForUser(userId: String): Flow<List<Service>> = serviceDao.getServicesForUser(userId)

    override suspend fun deleteServicesForVehicle(vehicleId: Int) {
        val snapshot = serviceCollection.whereEqualTo("vehicleId", vehicleId).get().await()
        if (!snapshot.isEmpty) {
            val batch = firestore.batch()
            for (doc in snapshot.documents) {
                batch.delete(doc.reference)
            }
            batch.commit().await()
        }
        serviceDao.deleteServicesForVehicle(vehicleId)
    }

    override suspend fun clearLocalServices() {
        serviceDao.deleteAllServices()
    }
}
