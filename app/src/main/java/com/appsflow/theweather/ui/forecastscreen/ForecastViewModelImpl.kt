package com.appsflow.theweather.ui.forecastscreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsflow.theweather.BuildConfig
import com.appsflow.theweather.domain.models.extra.forecast.Daily
import com.appsflow.theweather.data.repositories.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ForecastViewModelImpl constructor(private val mainRepository: com.appsflow.theweather.data.repositories.MainRepository) : ViewModel() {
    private val apiKey = BuildConfig.OPEN_WEATHER_API_KEY
    private val excludeArgs = "current,minutely,hourly,alerts"
    val dailyForecast: MutableLiveData<List<com.appsflow.theweather.domain.models.extra.forecast.Daily>> by lazy { MutableLiveData<List<com.appsflow.theweather.domain.models.extra.forecast.Daily>>() }
    val loading: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val errorMessage: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    fun getForecastResponse(lat: String, lon: String, units: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            loading.postValue(true)
            val response = mainRepository.getOnecallForecast(lat, lon, units, excludeArgs, apiKey)
            if(response.isSuccessful){
                withContext(Dispatchers.Main){
                    loading.postValue(false)
                    dailyForecast.postValue(response.body()?.daily)
                }
            } else {
                onError(response.message())
            }
        }
    }

    private fun onError(message: String) = viewModelScope.launch {
        errorMessage.value = message
        loading.value = false
    }

}