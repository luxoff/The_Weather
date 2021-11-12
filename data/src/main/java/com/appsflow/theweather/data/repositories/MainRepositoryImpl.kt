package com.appsflow.theweather.data.repositories

import com.appsflow.theweather.domain.repositories.MainRepository

class MainRepositoryImpl constructor(
    private val weatherService: com.appsflow.theweather.data.apiservice.WeatherAPI
    ): MainRepository {

    override suspend fun getWeatherResponse(apiKey: String, lat: String, lon: String, units: String) =
        weatherService.getWeatherResponse(lat, lon, units, apiKey)

    override suspend fun getOnecallForecast(
        lat: String, lon: String,
        units: String, excludeParams: String, apiKey: String
    ) = weatherService.getOnecallForecast(lat, lon, units, excludeParams, apiKey)
}