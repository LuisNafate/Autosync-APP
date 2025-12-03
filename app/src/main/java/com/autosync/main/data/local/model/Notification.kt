package com.autosync.main.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val message: String,
    val type: NotificationType,
    val date: Date,
    val isRead: Boolean = false,
    val relatedEntityId: Int? = null
)

enum class NotificationType {
    SERVICE_UPCOMING,
    SERVICE_REGISTERED,
    VEHICLE_REGISTERED,
    INVOICE_GENERATED,
    BATTERY_WARNING,
    MAINTENANCE_DUE
}