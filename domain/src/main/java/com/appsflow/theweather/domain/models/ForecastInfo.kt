package com.appsflow.theweather.domain.models

import com.appsflow.theweather.domain.models.extra.forecast.Daily

data class ForecastInfo(
    val daily: List<Daily>
)