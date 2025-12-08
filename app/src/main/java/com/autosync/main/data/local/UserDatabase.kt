package com.autosync.main.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.autosync.main.data.local.dao.ServiceDao
import com.autosync.main.data.local.dao.VehicleDao
import com.autosync.main.data.local.model.Service
import com.autosync.main.data.local.model.Vehicle

@Database(entities = [UserEntity::class, Vehicle::class, Service::class], version = 5, exportSchema = false)
@TypeConverters(Converters::class)
abstract class UserDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun vehicleDao(): VehicleDao
    abstract fun serviceDao(): ServiceDao
}
