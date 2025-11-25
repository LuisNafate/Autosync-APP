package com.autosync.main.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
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
