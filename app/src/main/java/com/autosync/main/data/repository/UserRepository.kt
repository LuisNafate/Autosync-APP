package com.autosync.main.data.repository

import com.autosync.main.data.local.dao.UserDao
import com.autosync.main.data.local.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface UserRepository {
    /**
     * Obtiene el perfil del usuario desde la base de datos local (Room).
     * Devuelve un Flow para que la UI se actualice automáticamente ante cualquier cambio.
     */
    fun getUser(uid: String): Flow<User?>

    /**
     * Descarga el perfil del usuario desde Firestore y lo guarda en la base de datos local (Room).
     */
    suspend fun syncUser(uid: String)
}

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userDao: UserDao
) : UserRepository {

    override fun getUser(uid: String): Flow<User?> {
        return userDao.getUser(uid)
    }

    override suspend fun syncUser(uid: String) {
        try {
            val userDoc = firestore.collection("users").document(uid).get().await()
            val user = userDoc.toObject(User::class.java)
            if (user != null) {
                // Nos aseguramos de que el UID está en el objeto antes de guardarlo
                userDao.insertUser(user.copy(uid = uid))
            }
        } catch (e: Exception) {
            // Manejar error, por ejemplo, si el usuario no tiene documento en Firestore
            // o hay un problema de red. Por ahora, no hacemos nada para no crashear.
        }
    }
}
