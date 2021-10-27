package com.appsflow.theweather.ui.forecastscreen.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appsflow.theweather.data.model.WeatherInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.InetAddress
import java.net.URL
import java.net.UnknownHostException

class ForecastViewModel : ViewModel() {
    private val apiKey = "214aa3c9e148c620ec34997d7af3a3f3"
    val weatherObject: MutableLiveData<WeatherInfo> by lazy { MutableLiveData<WeatherInfo>() }

    fun getForecastInfo(lon: String, lat: String) = viewModelScope.launch {
        withContext(Dispatchers.IO){
            val response = getApiForecastResponse(lon, lat, apiKey)
            val jsonWeatherObj =
                JSONObject(response ?: "{}")
            val list = jsonWeatherObj.getJSONArray("list")

        }
    }

    private fun getApiForecastResponse(lon: String, lat: String, apiKey: String): String? {
        val apiUrl = "https://api.openweathermap.org/data/2.5/forecast?" +
                "lat=$lat&lon=$lon&appid=$apiKey&cnt=5&units=metric"
        //Catching possible UnknownHostException for the first time
        try {
            val i: InetAddress = InetAddress.getByName(apiUrl)
        } catch (e1: UnknownHostException) {
            e1.printStackTrace()
        }

        //Trying actual connection
        val response: String? = try{
            URL(apiUrl).readText(charset("UTF-8"))
        } catch (ex: Exception){
            ex.let { Log.e("WeatherViewModel", "error: $it") }
            return null
        }
        return response
    }
}