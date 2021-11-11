package com.appsflow.theweather.data.model

import com.appsflow.theweather.data.model.extra.forecast.Daily

data class ForecastInfo(
    val daily: List<Daily>
)