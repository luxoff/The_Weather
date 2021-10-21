package com.appsflow.theweather.ui.forecast.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.appsflow.theweather.R
import com.appsflow.theweather.data.model.ForecastWeatherInfo
import com.appsflow.theweather.databinding.FragmentForecastBinding
import com.appsflow.theweather.ui.forecast.viewmodel.ForecastViewModel

class ForecastFragment : Fragment(R.layout.fragment_forecast) {
    private lateinit var forecastListAdapter: ForecastListAdapter
    private val forecastList = mutableListOf<ForecastWeatherInfo>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel: ForecastViewModel by viewModels()
        val binding = FragmentForecastBinding.bind(view)

        forecastListAdapter = ForecastListAdapter(forecastList)
        binding.apply {
            recyclerView.adapter = forecastListAdapter
            recyclerView.layoutManager = LinearLayoutManager(requireActivity())

            //hardcoded data for recycler view test
            forecastList.add(
                ForecastWeatherInfo(
                    status = "Cloudy",
                    minTemp = "9°C",
                    maxTemp = "12°C",
                    datetime = "Mon, 22/11"
                )
            )
            forecastListAdapter.notifyItemInserted(forecastList.size - 1)
            forecastList.add(
                ForecastWeatherInfo(
                    status = "Sunny",
                    minTemp = "9°C",
                    maxTemp = "13°C",
                    datetime = "Tue, 23/11"
                )
            )
            forecastListAdapter.notifyItemInserted(forecastList.size - 1)
            forecastList.add(
                ForecastWeatherInfo(
                    status = "Cloudy",
                    minTemp = "9°C",
                    maxTemp = "12°C",
                    datetime = "Wed, 24/11"
                )
            )
            forecastListAdapter.notifyItemInserted(forecastList.size - 1)
            forecastList.add(
                ForecastWeatherInfo(
                    status = "Rainy",
                    minTemp = "5°C",
                    maxTemp = "10°C",
                    datetime = "Thu, 25/11"
                )
            )
            forecastListAdapter.notifyItemInserted(forecastList.size - 1)
            forecastList.add(
                ForecastWeatherInfo(
                    status = "Cloudy",
                    minTemp = "9°C",
                    maxTemp = "12°C",
                    datetime = "Fri, 26/11"
                )
            )
            forecastListAdapter.notifyItemInserted(forecastList.size - 1)
        }

    }
}