package com.autosync.main.di

import com.autosync.main.data.repository.NotificationRepository
import com.autosync.main.data.repository.NotificationRepositoryImpl
import com.autosync.main.data.repository.ServiceRepository
import com.autosync.main.data.repository.ServiceRepositoryImpl
import com.autosync.main.data.repository.VehicleRepository
import com.autosync.main.data.repository.VehicleRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindVehicleRepository(impl: VehicleRepositoryImpl): VehicleRepository

    @Binds
    @Singleton
    abstract fun bindServiceRepository(impl: ServiceRepositoryImpl): ServiceRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository
}