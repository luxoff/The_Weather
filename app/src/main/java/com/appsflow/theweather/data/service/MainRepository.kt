package com.appsflow.theweather.data.service

import com.appsflow.theweather.data.service.network.WeatherAPI

class MainRepository constructor(private val weatherService: WeatherAPI) {

    suspend fun getWeatherResponse(apiKey: String, lat: String, lon: String, units: String) =
        weatherService.getWeatherResponse(lat, lon, units, apiKey)
}