package com.autosync.main.data.repository

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
    }

    suspend fun obtenerUsuario(uid: String): User? {
        return db.collection("users").document(uid).get().await().toObject(User::class.java)
    }
}
