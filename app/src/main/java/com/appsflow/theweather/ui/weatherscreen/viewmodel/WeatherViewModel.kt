package com.appsflow.theweather.ui.weatherscreen.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsflow.theweather.data.model.WeatherInfo
import com.appsflow.theweather.data.service.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherViewModel constructor(private val mainRepository: MainRepository) : ViewModel() {
    private val apiKey = "214aa3c9e148c620ec34997d7af3a3f3"
    val weatherObject: MutableLiveData<WeatherInfo> by lazy { MutableLiveData<WeatherInfo>() }
    val loading: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val errorMessage: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    fun getWeatherResponse(lat: String, lon: String, units: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            loading.postValue(true)
            val response = mainRepository.getWeatherResponse(apiKey, lat, lon, units)
            if (response.isSuccessful) {
                withContext(Dispatchers.Main){
                    loading.postValue(false)
                    weatherObject.postValue(response.body())
                }
            } else {
                // display error message
                onError(response.message())
            }
        }
    }

    private fun onError(message: String) = viewModelScope.launch {
        errorMessage.value = message
        loading.value = false
    }
}