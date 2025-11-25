package com.autosync.main.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.autosync.main.data.local.dao.ServiceDao
import com.autosync.main.data.local.dao.UserDao
import com.autosync.main.data.local.dao.VehicleDao
import com.autosync.main.data.local.model.Service
import com.autosync.main.data.local.model.User
import com.autosync.main.data.local.model.Vehicle

@Database(entities = [Vehicle::class, Service::class, User::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun serviceDao(): ServiceDao
    abstract fun userDao(): UserDao
}
