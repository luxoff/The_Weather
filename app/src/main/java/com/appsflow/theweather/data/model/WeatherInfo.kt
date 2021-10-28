package com.appsflow.theweather.data.model


import com.appsflow.theweather.data.model.extra.Coord
import com.appsflow.theweather.data.model.extra.Main
import com.appsflow.theweather.data.model.extra.Sys
import com.appsflow.theweather.data.model.extra.Wind
import com.appsflow.theweather.data.model.extra.forecast.Weather
import com.google.gson.annotations.SerializedName

data class WeatherInfo(
    @SerializedName("cod")
    val code: Int,
    val coord: Coord,
    @SerializedName("dt")
    val deltaTime: Int,
    @SerializedName("main")
    val mainInfo: Main,
    @SerializedName("name")
    val cityName: String,
    @SerializedName("sys")
    val systemInfo: Sys,
    @SerializedName("timezone")
    val timeZone: Int,
    val visibility: Int,
    @SerializedName("weather")
    val weatherDescription: List<Weather>,
    @SerializedName("wind")
    val windInfo: Wind
)