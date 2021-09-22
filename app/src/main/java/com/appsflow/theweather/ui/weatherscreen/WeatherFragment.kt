package com.appsflow.theweather.ui.weatherscreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.appsflow.theweather.R
import com.appsflow.theweather.databinding.FragmentWeatherBinding


class WeatherFragment : Fragment(R.layout.fragment_weather) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel: WeatherViewModel by viewModels()
        val binding = FragmentWeatherBinding.bind(view)

        binding.apply {

        }
    }
}