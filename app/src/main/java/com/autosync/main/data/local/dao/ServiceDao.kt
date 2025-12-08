package com.autosync.main.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.autosync.main.data.local.model.Service
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: Service): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServices(services: List<Service>)

    @Update
    suspend fun updateService(service: Service)

    @Delete
    suspend fun deleteService(service: Service)

    @Query("DELETE FROM services WHERE userId = :userId")
    suspend fun clearUserServices(userId: String)

    @Query("DELETE FROM services")
    suspend fun deleteAllServices()

    @Query("DELETE FROM services WHERE vehicleId = :vehicleId")
    suspend fun deleteServicesForVehicle(vehicleId: Int)

    @Query("SELECT * FROM services WHERE vehicleId = :vehicleId ORDER BY date DESC")
    fun getServicesForVehicle(vehicleId: Int): Flow<List<Service>>

    @Query("SELECT * FROM services WHERE id = :serviceId")
    fun getServiceById(serviceId: Int): Flow<Service?>

    @Query("SELECT * FROM services WHERE userId = :userId ORDER BY date DESC")
    fun getServicesForUser(userId: String): Flow<List<Service>>

}
