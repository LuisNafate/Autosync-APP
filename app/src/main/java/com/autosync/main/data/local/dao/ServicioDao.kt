package com.autosync.main.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.autosync.main.data.local.entities.Servicio
import kotlinx.coroutines.flow.Flow

@Dao
interface ServicioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServicio(servicio: Servicio)

    @Update
    suspend fun updateServicio(servicio: Servicio)

    @Delete
    suspend fun deleteServicio(servicio: Servicio)

    @Query("SELECT * FROM servicios ORDER BY fecha DESC")
    fun getAllServicios(): Flow<List<Servicio>>

    @Query("SELECT * FROM servicios WHERE vehicleId = :vehicleId ORDER BY fecha DESC")
    fun getServiciosByVehicle(vehicleId: Int): Flow<List<Servicio>>

    @Query("SELECT * FROM servicios WHERE id = :id")
    suspend fun getServicioById(id: Int): Servicio?

    @Query("SELECT * FROM servicios WHERE fecha >= :startDate AND fecha <= :endDate ORDER BY fecha DESC")
    fun getServiciosByDateRange(startDate: Long, endDate: Long): Flow<List<Servicio>>
}