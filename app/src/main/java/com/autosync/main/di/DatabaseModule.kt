package com.autosync.main.di

import android.content.Context
import androidx.room.Room
import com.autosync.main.data.local.UserDatabase
import com.autosync.main.data.local.UserDao
import com.autosync.main.data.local.dao.ServiceDao
import com.autosync.main.data.local.dao.VehicleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideUserDatabase(@ApplicationContext context: Context): UserDatabase {
        return Room.databaseBuilder(
            context,
            UserDatabase::class.java,
            "user_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideUserDao(database: UserDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideVehicleDao(database: UserDatabase): VehicleDao {
        return database.vehicleDao()
    }

    @Provides
    fun provideServiceDao(database: UserDatabase): ServiceDao {
        return database.serviceDao()
    }
}
