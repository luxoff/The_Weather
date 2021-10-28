package com.appsflow.theweather.ui.forecastscreen.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsflow.theweather.data.model.extra.forecast.Daily
import com.appsflow.theweather.data.service.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ForecastViewModel constructor(private val mainRepository: MainRepository) : ViewModel() {
    private val apiKey = "214aa3c9e148c620ec34997d7af3a3f3"
    private val excludeArgs = "current,minutely,hourly,alerts"
    val dailyForecast: MutableLiveData<List<Daily>> by lazy { MutableLiveData<List<Daily>>() }
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