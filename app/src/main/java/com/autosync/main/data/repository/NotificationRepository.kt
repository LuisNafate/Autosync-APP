package com.autosync.main.data.repository

import com.autosync.main.data.local.dao.NotificationDao
import com.autosync.main.data.local.model.Notification
import com.autosync.main.data.local.model.NotificationType
import com.autosync.main.util.NotificationHelper
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

interface NotificationRepository {
    fun getAllNotifications(): Flow<List<Notification>>
    fun getUnreadNotifications(): Flow<List<Notification>>
    fun getUnreadCount(): Flow<Int>
    suspend fun insertNotification(notification: Notification)
    suspend fun markAsRead(notificationId: Int)
    suspend fun markAllAsRead()
    suspend fun deleteNotification(notificationId: Int)
    suspend fun deleteAllNotifications()
    suspend fun createServiceRegisteredNotification(vehicleName: String, serviceType: String, serviceId: Int)
    suspend fun createVehicleRegisteredNotification(vehicleName: String, vehicleId: Int)
    suspend fun createInvoiceGeneratedNotification(vehicleName: String, serviceId: Int)
    suspend fun createMaintenanceDueNotification(vehicleName: String, serviceType: String, vehicleId: Int)
}

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val notificationDao: NotificationDao,
    private val notificationHelper: NotificationHelper
) : NotificationRepository {

    override fun getAllNotifications(): Flow<List<Notification>> =
        notificationDao.getAllNotifications()

    override fun getUnreadNotifications(): Flow<List<Notification>> =
        notificationDao.getUnreadNotifications()

    override fun getUnreadCount(): Flow<Int> =
        notificationDao.getUnreadCount()

    override suspend fun insertNotification(notification: Notification) =
        notificationDao.insertNotification(notification)

    override suspend fun markAsRead(notificationId: Int) =
        notificationDao.markAsRead(notificationId)

    override suspend fun markAllAsRead() =
        notificationDao.markAllAsRead()

    override suspend fun deleteNotification(notificationId: Int) =
        notificationDao.deleteNotification(notificationId)

    override suspend fun deleteAllNotifications() =
        notificationDao.deleteAllNotifications()

    override suspend fun createServiceRegisteredNotification(
        vehicleName: String,
        serviceType: String,
        serviceId: Int
    ) {
        val notification = Notification(
            title = "Servicio Próximo",
            message = "Tu $vehicleName ${serviceType.lowercase()} requiere cambio de aceite recomendado.",
            type = NotificationType.SERVICE_REGISTERED,
            date = Date(),
            relatedEntityId = serviceId
        )
        insertNotification(notification)
        notificationHelper.showNotification(
            title = notification.title,
            message = notification.message,
            type = notification.type
        )
    }

    override suspend fun createVehicleRegisteredNotification(
        vehicleName: String,
        vehicleId: Int
    ) {
        val notification = Notification(
            title = "Nuevo Vehículo",
            message = "Se ha registrado exitosamente el vehículo $vehicleName",
            type = NotificationType.VEHICLE_REGISTERED,
            date = Date(),
            relatedEntityId = vehicleId
        )
        insertNotification(notification)
        notificationHelper.showNotification(
            title = notification.title,
            message = notification.message,
            type = notification.type
        )
    }

    override suspend fun createInvoiceGeneratedNotification(
        vehicleName: String,
        serviceId: Int
    ) {
        val notification = Notification(
            title = "Factura Generada",
            message = "Se generó la factura del servicio de frenos.",
            type = NotificationType.INVOICE_GENERATED,
            date = Date(),
            relatedEntityId = serviceId
        )
        insertNotification(notification)
        notificationHelper.showNotification(
            title = notification.title,
            message = notification.message,
            type = notification.type
        )
    }

    override suspend fun createMaintenanceDueNotification(
        vehicleName: String,
        serviceType: String,
        vehicleId: Int
    ) {
        val notification = Notification(
            title = "Afinación Mayor",
            message = "Servicio completado en Taller Norte.",
            type = NotificationType.MAINTENANCE_DUE,
            date = Date(),
            relatedEntityId = vehicleId
        )
        insertNotification(notification)
        notificationHelper.showNotification(
            title = notification.title,
            message = notification.message,
            type = notification.type
        )
    }
}