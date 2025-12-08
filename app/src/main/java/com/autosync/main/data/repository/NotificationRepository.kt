package com.autosync.main.data.repository

import com.autosync.main.data.local.model.Notification
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import com.autosync.main.notification.NotificationHelper

interface NotificationRepository {
    suspend fun createNotification(notification: Notification)
    fun getUserNotifications(userId: String): Flow<List<Notification>>
    suspend fun markAsRead(notificationId: String)
    suspend fun deleteAllUserNotifications(userId: String)
}

class NotificationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) : NotificationRepository {

    private val collection = firestore.collection("notifications")

    override suspend fun createNotification(notification: Notification) {
        val docRef = collection.document()
        val notifWithId = notification.copy(id = docRef.id)
        docRef.set(notifWithId).await()
        NotificationHelper.showNotification(context, "AutoSync", notification.message)
    }

    override fun getUserNotifications(userId: String): Flow<List<Notification>> = callbackFlow {
        val subscription = collection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (snapshot != null) {
                    val notifications = snapshot.toObjects(Notification::class.java)
                    // Sort in memory to avoid needing a Firestore composite index
                    val sortedNotifications = notifications.sortedByDescending { it.date }
                    trySend(sortedNotifications)
                } else {
                }
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun markAsRead(notificationId: String) {
        collection.document(notificationId).update("read", true).await()
    }

    override suspend fun deleteAllUserNotifications(userId: String) {
        val snapshot = collection.whereEqualTo("userId", userId).get().await()
        if (!snapshot.isEmpty) {
            val batch = firestore.batch()
            for (doc in snapshot.documents) {
                batch.delete(doc.reference)
            }
            batch.commit().await()
        }
    }
}
