package com.autosync.main.data.remote.nhtsa.dto

import com.google.gson.annotations.SerializedName

data class ModelDto(
    @SerializedName("Make_ID")
    val makeId: Int,
    @SerializedName("Make_Name")
    val makeName: String,
    @SerializedName("Model_ID")
    val modelId: Int,
    @SerializedName("Model_Name")
    val modelName: String
)
