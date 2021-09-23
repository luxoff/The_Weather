package com.appsflow.theweather.ui.weatherscreen.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.appsflow.theweather.R
import com.appsflow.theweather.data.model.WeatherInfo
import com.appsflow.theweather.databinding.FragmentWeatherBinding
import com.appsflow.theweather.ui.weatherscreen.viewmodel.WeatherViewModel


class WeatherFragment : Fragment(R.layout.fragment_weather) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel: WeatherViewModel by viewModels()
        val binding = FragmentWeatherBinding.bind(view)

        binding.apply {
            viewModel.getWeatherInfo("kyiv,ua")
            val weatherObjectObserver = Observer<WeatherInfo> {
                tvLocation.text = it.location
                tvUpdatedAt.text = it.updatedAt
                tvCurrentWeatherStatus.text = it.status
                tvTemperature.text = it.temp
                tvMinTemp.text = it.minTemp
                tvMaxTemp.text = it.maxTemp
                tvPressure.text = it.pressure
                tvHumidity.text = it.humidity
                tvWind.text = it.wind
                tvSunrise.text = it.sunrise
                tvSunset.text  = it.sunset
            }
            viewModel.weatherObject.observe(requireActivity(), weatherObjectObserver)
        }
    }
}