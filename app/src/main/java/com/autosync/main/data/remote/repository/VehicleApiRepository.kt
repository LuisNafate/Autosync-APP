package com.autosync.main.data.remote.repository

import android.util.Log
import com.autosync.main.data.remote.VehicleApiService
import com.autosync.main.data.remote.dto.CarDto
import javax.inject.Inject

class VehicleApiRepository @Inject constructor(
    private val apiService: VehicleApiService
) {

    suspend fun getCarsByMake(make: String): List<CarDto> {
        return try {
            apiService.getCarsByMake(make = make)
        } catch (e: Exception) {
            Log.e("VehicleApiRepository", "Error fetching cars: ", e)
            emptyList()
        }
    }
}
