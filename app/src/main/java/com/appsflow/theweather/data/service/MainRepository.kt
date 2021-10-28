package com.appsflow.theweather.data.service

import com.appsflow.theweather.data.service.network.WeatherAPI

class MainRepository constructor(private val weatherService: WeatherAPI) {

    suspend fun getWeatherResponse(apiKey: String, lat: String, lon: String, units: String) =
        weatherService.getWeatherResponse(lat, lon, units, apiKey)

    suspend fun getOnecallForecast(
        lat: String, lon: String,
        units: String, excludeParams: String, apiKey: String
    ) = weatherService.getOnecallForecast(lat, lon, units, excludeParams, apiKey)
}