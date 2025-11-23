package com.autosync.main.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.autosync.main.data.local.model.Vehicle

@Entity(
    tableName = "servicios",
    foreignKeys = [
        ForeignKey(
            entity = Vehicle::class,
            parentColumns = ["id"],
            childColumns = ["vehicleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["vehicleId"])]
)
data class Servicio(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val vehicleId: Int,
    val tipoServicio: String,
    val categoria: String,
    val taller: String,
    val fecha: Long,
    val descripcion: String,
    val costo: Double = 0.0,
    val proximoServicio: Long? = null
)