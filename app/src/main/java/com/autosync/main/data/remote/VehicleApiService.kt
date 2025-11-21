package com.autosync.main.data.remote

import com.autosync.main.data.remote.dto.CarDto
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface VehicleApiService {

    @GET("cars")
    suspend fun getCarsByMake(
        @Query("make") make: String,
        @Header("X-Api-Key") apiKey: String = "bdtsZN3uovHOde20wP1hGg==36itdE0mmdaL0V3h"
    ): List<CarDto>

}
