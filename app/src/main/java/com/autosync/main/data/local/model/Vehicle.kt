package com.autosync.main.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicles")
data class Vehicle(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String = "",
    val make: String = "",
    val model: String = "",
    val year: Int = 0,
    val licensePlate: String = "",
    val imageUri: String? = null
)
