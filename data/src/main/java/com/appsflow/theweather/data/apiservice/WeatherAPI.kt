package com.appsflow.theweather.data.apiservice

import com.appsflow.theweather.domain.models.ForecastInfo
import com.appsflow.theweather.domain.models.WeatherInfo
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {

    @GET("weather")
    suspend fun getWeatherResponse(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("units") units: String,
        @Query("appid") apiKey: String,
    ): Response<com.appsflow.theweather.domain.models.WeatherInfo>

    @GET("onecall")
    suspend fun getOnecallForecast(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("units") units: String,
        @Query("exclude") exclude: String,
        @Query("appid") apiKey: String,
    ): Response<com.appsflow.theweather.domain.models.ForecastInfo>

    companion object {
        fun getWeatherService(): WeatherAPI {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherAPI::class.java)
        }

        const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    }
}