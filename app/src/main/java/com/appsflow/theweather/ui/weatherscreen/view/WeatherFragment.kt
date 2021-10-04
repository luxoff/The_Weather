package com.appsflow.theweather.ui.weatherscreen.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.appsflow.theweather.R
import com.appsflow.theweather.data.model.WeatherInfo
import com.appsflow.theweather.databinding.FragmentWeatherBinding
import com.appsflow.theweather.ui.weatherscreen.viewmodel.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import java.math.RoundingMode
import java.text.DecimalFormat


class WeatherFragment : Fragment(R.layout.fragment_weather) {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    Log.i("Permission: ", "Granted")
                } else {
                    Log.i("Permission: ", "Denied")
                }
            }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel: WeatherViewModel by viewModels()
        val binding = FragmentWeatherBinding.bind(view)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.apply {
            val df = DecimalFormat("#")
            df.roundingMode = RoundingMode.CEILING

            getWeatherWithPermissionsGranted(view, viewModel)

            btnRefresh.setOnClickListener {
                val animation: Animation =
                    AnimationUtils.loadAnimation(requireContext(), R.anim.refresh_clockwise_animation)
                it.startAnimation(animation)

                getWeatherWithPermissionsGranted(view, viewModel)
            }

            //viewModel.getWeatherInfo("kyiv,ua")

            val weatherObjectObserver = Observer<WeatherInfo> {
                tvLocation.text = it.location
                tvUpdatedAt.text = it.updatedAt
                tvCurrentWeatherStatus.text = it.status.replaceFirstChar { it -> it.uppercase() }
                tvTemperature.text = it.temp.substringBefore(".") + "°C"
                tvMinTemp.text = it.minTemp.substringBefore(".") + "°C"
                tvMaxTemp.text = it.maxTemp.substringBefore(".") + "°C"
                tvPressure.text = it.pressure
                tvHumidity.text = it.humidity
                tvWind.text = it.wind
                tvSunrise.text = it.sunrise
                tvSunset.text = it.sunset

                val animation: Animation =
                    AnimationUtils.loadAnimation(requireContext(), R.anim.fade_temp)
                tvTemperature.startAnimation(animation)
            }
            viewModel.weatherObject.observe(requireActivity(), weatherObjectObserver)
        }
    }

    private fun getWeatherWithPermissionsGranted(view: View, viewModel: WeatherViewModel){
        when{
            ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener {
                        viewModel.getWeatherInfo(
                            lon = it.longitude.toString(),
                            lat = it.latitude.toString()
                        )
                    }
            }
            ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Snackbar.make(view,
                    getString(R.string.location_permission_rationale),
                    Snackbar.LENGTH_LONG).show()
            }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        }
    }

}