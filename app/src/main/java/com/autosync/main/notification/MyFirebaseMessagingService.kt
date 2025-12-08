package com.autosync.main.notification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let {
            NotificationHelper.showNotification(applicationContext, it.title ?: "", it.body ?: "")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}
