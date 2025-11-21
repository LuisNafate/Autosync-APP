package com.autosync.main.data.remote

import com.autosync.main.data.remote.nhtsa.dto.ModelsForMakeResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface VehicleApiService {

    @GET("vehicles/GetModelsForMake/{make}?format=json")
    suspend fun getModelsForMake(
        @Path("make") make: String
    ): ModelsForMakeResponse

}
