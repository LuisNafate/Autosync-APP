package com.autosync.main.data.repository

import com.autosync.main.data.local.UserDao
import com.autosync.main.data.local.UserEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {

    private val db = FirebaseFirestore.getInstance()

    suspend fun guardarUsuario(uid: String, nombre: String, email: String) {
        val user = UserEntity(uid, nombre, email)
        db.collection("users").document(uid).set(user).await()
        userDao.insertUser(user)
    }

    fun obtenerUsuario(uid: String): Flow<UserEntity?> = flow {
        // 1. Try to get user from local database (Room)
        var user = userDao.getUser(uid)
        emit(user)

        // 2. If not in Room, get from Firestore
        if (user == null) {
            val snapshot = db.collection("users").document(uid).get().await()
            user = snapshot.toObject(UserEntity::class.java)

            // 3. Save to Room for future access
            user?.let {
                userDao.insertUser(it)
                emit(it) // Emit the user from Firestore
            }
        }
    }
}
