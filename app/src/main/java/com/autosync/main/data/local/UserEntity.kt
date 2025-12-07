package com.autosync.main.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val uid: String = "",
    val nombre: String = "",
    val email: String = ""
)
