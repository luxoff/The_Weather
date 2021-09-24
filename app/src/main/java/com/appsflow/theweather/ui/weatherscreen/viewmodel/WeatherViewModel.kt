package com.appsflow.theweather.ui.weatherscreen.viewmodel

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
import java.text.SimpleDateFormat
import java.util.*

class WeatherViewModel() : ViewModel(){
    private val apiKey = "214aa3c9e148c620ec34997d7af3a3f3"
    val weatherObject: MutableLiveData<WeatherInfo> by lazy { MutableLiveData<WeatherInfo>() }

    fun getWeatherInfo(lon: String, lat: String) = viewModelScope.launch{
        withContext(Dispatchers.IO) {
            val response = getWeatherResponse(lon, lat, apiKey)
            val jsonWeatherObj =
                JSONObject(response ?: "{}")
            val main = jsonWeatherObj.getJSONObject("main")
            val sys = jsonWeatherObj.getJSONObject("sys")
            val wind = jsonWeatherObj.getJSONObject("wind")
            val weather = jsonWeatherObj.getJSONArray("weather").getJSONObject(0)

            val temp = main.getString("temp") + "°C"
            val tempMin = "Min Temp: " + main.getString("temp_min") + "°C"
            val tempMax = "Max Temp: " + main.getString("temp_max") + "°C"
            val pressure = main.getString("pressure")
            val humidity = main.getString("humidity")
            val sunrise: Long = sys.getLong("sunrise")
            val sunset: Long = sys.getLong("sunset")
            val windSpeed = wind.getString("speed")
            val weatherDescription = weather.getString("description")

            val fullLocationName =
                jsonWeatherObj.getString("name") + ", " + sys.getString("country")
            val updatedAt: Long = jsonWeatherObj.getLong("dt")
            val updatedAtText = "Updated ${
                SimpleDateFormat(
                    "dd/MM/yyyy 'at' hh:mm a", Locale.ENGLISH
                ).format(Date(updatedAt * 1000))
            }"

            val sunriseText = SimpleDateFormat(
                "hh:mm a", Locale.ENGLISH
            ).format(Date(sunrise * 1000))
            val sunsetText = SimpleDateFormat(
                "hh:mm a", Locale.ENGLISH
            ).format(Date(sunset * 1000))

            weatherObject.postValue(
                WeatherInfo(
                fullLocationName, temp, tempMin, tempMax, updatedAtText, weatherDescription,
                "", pressure, humidity, windSpeed, sunriseText, sunsetText
                )
            )
        }
    }

    private fun getWeatherResponse(lon: String, lat: String, apiKey: String): String? {
        val apiUrl = "https://api.openweathermap.org/data/2.5/weather?" +
                "lat=$lat&lon=$lon&appid=$apiKey&units=metric"
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