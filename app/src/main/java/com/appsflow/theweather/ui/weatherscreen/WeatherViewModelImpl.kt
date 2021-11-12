package com.appsflow.theweather.ui.weatherscreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsflow.theweather.BuildConfig
import com.appsflow.theweather.domain.models.WeatherInfo
import com.appsflow.theweather.data.repositories.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherViewModelImpl constructor(private val mainRepository: com.appsflow.theweather.data.repositories.MainRepository) : ViewModel() {
    private val apiKey = BuildConfig.OPEN_WEATHER_API_KEY
    val weatherObject: MutableLiveData<com.appsflow.theweather.domain.models.WeatherInfo> by lazy { MutableLiveData<com.appsflow.theweather.domain.models.WeatherInfo>() }
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