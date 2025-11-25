package com.autosync.main.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa un usuario en la aplicación. 
 * Es una entidad de Room para el cacheo local.
 * Los valores por defecto son necesarios para que Firestore pueda deserializar los documentos a este objeto.
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val uid: String = "",
    val nombre: String = "",
    val email: String = ""
)
