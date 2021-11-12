package com.appsflow.theweather.domain.repositories

import com.appsflow.theweather.domain.models.ForecastInfo
import com.appsflow.theweather.domain.models.WeatherInfo
import retrofit2.Response

interface MainRepository {
    suspend fun getWeatherResponse(
        apiKey: String,
        lat: String,
        lon: String,
        units: String
    ): Response<WeatherInfo>

    suspend fun getOnecallForecast(
        lat: String, lon: String,
        units: String, excludeParams: String, apiKey: String
    ): Response<ForecastInfo>
}