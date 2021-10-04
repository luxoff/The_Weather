package com.appsflow.theweather.data.model

data class WeatherInfo(val location: String,
                       val temp: String,
                       val minTemp: String,
                       val maxTemp: String,
                       val updatedAt: String,
                       val status: String,
                       val airQualityIndex: String,
                       val pressure: String,
                       val humidity: String,
                       val wind: String,
                       val sunrise: String,
                       val sunset: String)