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
        userDao.insertUser(UserEntity(uid, nombre, email))
    }

    suspend fun obtenerUsuario(uid: String): User? {
        val localUser = userDao.getUser(uid)
        if (localUser != null) {
            return User(localUser.nombre, localUser.email)
        }

        val remoteUser = db.collection("users").document(uid).get().await().toObject(User::class.java)
        if (remoteUser != null) {
            userDao.insertUser(UserEntity(uid, remoteUser.nombre, remoteUser.email))
        }
        return remoteUser
    }
}
