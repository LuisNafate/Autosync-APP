package com.autosync.main.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
<<<<<<< HEAD
import androidx.room.TypeConverters
import com.autosync.main.data.local.dao.ServiceDao
import com.autosync.main.data.local.dao.VehicleDao
import com.autosync.main.data.local.model.Service
import com.autosync.main.data.local.model.Vehicle

@Database(entities = [Vehicle::class, Service::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun serviceDao(): ServiceDao
}
=======
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
>>>>>>> aca5c66e22d681fc3460be710c9a9fa1c4680682
