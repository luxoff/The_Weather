package com.appsflow.theweather.ui.weatheralertscreen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.appsflow.theweather.R
import com.appsflow.theweather.databinding.FragmentWeatherAlertBinding

class WeatherAlertFragment : Fragment(R.layout.fragment_weather_alert) {
    val args: WeatherAlertFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentWeatherAlertBinding.bind(view)
        val dangerLevel = args.dangerLevel
        binding.tvDangerLevelIndex.text = dangerLevel.toString()
    }
}