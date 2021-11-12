package com.appsflow.theweather.domain.models.extra


import com.google.gson.annotations.SerializedName

data class Wind(
    @SerializedName("deg")
    val degrees: Int,
    val gust: Double,
    val speed: Double
)