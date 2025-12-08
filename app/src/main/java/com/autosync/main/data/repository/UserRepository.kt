package com.autosync.main.data.repository

import com.autosync.main.data.local.UserDao
import com.autosync.main.data.local.UserEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface UserRepository {
    suspend fun guardarUsuario(uid: String, nombre: String, email: String)
    fun obtenerUsuario(uid: String): Flow<UserEntity?>

    suspend fun syncUser(uid: String)
    suspend fun deleteUser(uid: String)
    suspend fun clearLocalUser()
}

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val vehicleRepository: VehicleRepository,
    private val notificationRepository: NotificationRepository
) : UserRepository {

    private val db = FirebaseFirestore.getInstance()

    override suspend fun guardarUsuario(uid: String, nombre: String, email: String) {
        val user = UserEntity(uid, nombre, email)
        db.collection("users").document(uid).set(user).await()
        userDao.insertUser(user)
    }

    override fun obtenerUsuario(uid: String): Flow<UserEntity?> = flow {
        var user = userDao.getUser(uid)
        emit(user)

        if (user == null) {
            val snapshot = db.collection("users").document(uid).get().await()
            user = snapshot.toObject(UserEntity::class.java)

            user?.let {
                userDao.insertUser(it)
                emit(it)
            }
        }
    }

    override suspend fun syncUser(uid: String) {
        try {
            val snapshot = db.collection("users").document(uid).get().await()
            val remoteUser = snapshot.toObject(UserEntity::class.java)
            remoteUser?.let {
                userDao.insertUser(it)
            }
        } catch (e: Exception) {
            // Handle errors
        }
    }

    override suspend fun deleteUser(uid: String) {
        // Cascade delete vehicles (which cascades services)
        vehicleRepository.deleteVehiclesForUser(uid)
        
        // Delete notifications
        notificationRepository.deleteAllUserNotifications(uid)
        
        // Delete user from Firestore
        db.collection("users").document(uid).delete().await()
        
        // Delete user from Room
        userDao.deleteUser(UserEntity(uid, "", "")) // Assuming DAO needs an entity to delete, or add clearUser(uid) query
    }

    override suspend fun clearLocalUser() {
        userDao.deleteAllUsers()
    }
}
