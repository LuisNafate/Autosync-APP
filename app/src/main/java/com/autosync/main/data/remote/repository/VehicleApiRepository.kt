package com.autosync.main.data.remote.repository

import android.util.Log
import com.autosync.main.data.remote.VehicleApiService
import com.autosync.main.data.remote.nhtsa.dto.ModelDto
import javax.inject.Inject

class VehicleApiRepository @Inject constructor(
    private val apiService: VehicleApiService
) {

    suspend fun getModelsForMake(make: String): List<ModelDto> {
        return try {
            val response = apiService.getModelsForMake(make = make)
            response.results
        } catch (e: Exception) {
            Log.e("VehicleApiRepository", "Error fetching models: ", e)
            emptyList()
        }
    }
}
