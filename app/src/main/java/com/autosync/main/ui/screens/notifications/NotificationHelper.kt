package com.autosync.main.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.autosync.main.MainActivity
import com.autosync.main.R
import com.autosync.main.data.local.model.NotificationType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val CHANNEL_ID = "autosync_notifications"
        private const val CHANNEL_NAME = "AutoSync Notificaciones"
        private const val CHANNEL_DESCRIPTION = "Notificaciones de servicios y vehículos"
        private var notificationId = 1000
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(
        title: String,
        message: String,
        type: NotificationType
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "notifications")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(getNotificationIcon(type))
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setColor(getNotificationColor(type))
            .build()

        NotificationManagerCompat.from(context).notify(notificationId++, notification)
    }

    private fun getNotificationIcon(type: NotificationType): Int {
        return when (type) {
            NotificationType.SERVICE_UPCOMING,
            NotificationType.SERVICE_REGISTERED,
            NotificationType.MAINTENANCE_DUE -> android.R.drawable.ic_menu_info_details
            NotificationType.VEHICLE_REGISTERED -> android.R.drawable.ic_menu_add
            NotificationType.INVOICE_GENERATED -> android.R.drawable.ic_menu_save
            NotificationType.BATTERY_WARNING -> android.R.drawable.ic_dialog_alert
        }
    }

    private fun getNotificationColor(type: NotificationType): Int {
        return when (type) {
            NotificationType.SERVICE_UPCOMING -> 0xFF3B82F6.toInt()
            NotificationType.SERVICE_REGISTERED -> 0xFF10B981.toInt()
            NotificationType.VEHICLE_REGISTERED -> 0xFF8B5CF6.toInt()
            NotificationType.INVOICE_GENERATED -> 0xFFF59E0B.toInt()
            NotificationType.BATTERY_WARNING -> 0xFFEF4444.toInt()
            NotificationType.MAINTENANCE_DUE -> 0xFF06B6D4.toInt()
        }
    }
}