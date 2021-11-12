package com.appsflow.theweather.domain.models.extra


import com.google.gson.annotations.SerializedName

data class Weather(
    val description: String,
    val id: Int,
    @SerializedName("main")
    val shortStatus: String
)