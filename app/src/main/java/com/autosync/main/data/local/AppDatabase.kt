package com.autosync.main.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.autosync.main.data.local.dao.ServicioDao
import com.autosync.main.data.local.dao.VehicleDao
import com.autosync.main.data.local.entities.Servicio
import com.autosync.main.data.local.model.Vehicle

@Database(
    entities = [Vehicle::class, Servicio::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun servicioDao(): ServicioDao
}