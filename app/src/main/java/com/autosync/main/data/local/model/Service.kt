package com.autosync.main.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "services",
    foreignKeys = [ForeignKey(
        entity = Vehicle::class,
        parentColumns = ["id"],
        childColumns = ["vehicleId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Service(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val vehicleId: Int = 0,
    val userId: String = "",
    val serviceType: String = "",
    val customService: String? = null,
    val workshop: String = "",
    val date: Date = Date(),
    val details: String? = null,
    val cost: Double? = null
)
