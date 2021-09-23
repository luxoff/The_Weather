package com.appsflow.theweather.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import java.nio.charset.StandardCharsets


class WeatherApiRepository() {

    suspend fun getWeatherResponse(locationCode: String, apiKey: String): String? {
        return withContext(Dispatchers.IO) {
            val apiUrl = "http://api.openweathermap.org/data/2.5/weather" +
                    "?q=$locationCode&appid=$apiKey"
            val response: String? = try{
                URL(apiUrl).readText(StandardCharsets.UTF_8)
            }catch (ex: Exception){
                null
            }
            return@withContext response
        }
    }
}

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}