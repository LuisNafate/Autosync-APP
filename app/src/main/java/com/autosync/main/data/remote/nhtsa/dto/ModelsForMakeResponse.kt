package com.autosync.main.data.remote.nhtsa.dto

import com.google.gson.annotations.SerializedName

data class ModelsForMakeResponse(
    @SerializedName("Results")
    val results: List<ModelDto>
)
