package com.autosync.main.data.repository

import com.autosync.main.data.local.UserDao
import com.autosync.main.data.local.UserEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

data class User(val nombre: String = "", val email: String = "")

@Singleton
class UserRepository @Inject constructor() {

    private val db = FirebaseFirestore.getInstance()

    suspend fun guardarUsuario(uid: String, nombre: String, email: String) {
        val user = hashMapOf(
            "nombre" to nombre,
            "email" to email
        )
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
