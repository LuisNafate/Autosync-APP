package com.autosync.main.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.autosync.main.data.local.model.Service
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: Service)

    @Query("SELECT * FROM services WHERE vehicleId = :vehicleId ORDER BY date DESC")
    fun getServicesForVehicle(vehicleId: Int): Flow<List<Service>>

    @Query("SELECT * FROM services WHERE id = :serviceId")
    fun getServiceById(serviceId: Int): Flow<Service?>

}
