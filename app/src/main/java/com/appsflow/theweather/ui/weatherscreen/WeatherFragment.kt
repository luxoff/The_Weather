package com.appsflow.theweather.ui.weatherscreen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.appsflow.theweather.R
import com.appsflow.theweather.data.repositories.MainRepository
import com.appsflow.theweather.databinding.FragmentWeatherBinding
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


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
                    Timber.i("Granted")
                } else {
                    Timber.i("Denied")
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentWeatherBinding.bind(view)
        val weatherService = com.appsflow.theweather.data.apiservice.WeatherAPI.getWeatherService()
        val mainRepository =
            com.appsflow.theweather.data.repositories.MainRepository(weatherService)
        val viewModel = WeatherViewModelImpl(mainRepository)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.apply {
            if (isNetworkAvailable(requireContext())) {
                lottieErrorAnimation.visibility = View.GONE
                getWeatherWithPermissionsGranted(view, viewModel)
            } else {
                weatherMainConstraintContainer.visibility = View.GONE
                lottieErrorAnimation.visibility = View.VISIBLE
                Snackbar.make(
                    view,
                    getString(R.string.network_error_message),
                    Snackbar.LENGTH_LONG
                ).show()
            }

            weatherSwipeRefreshLayout.setOnRefreshListener {
                if (isNetworkAvailable(requireContext())) {
                    lottieErrorAnimation.visibility = View.GONE
                    getWeatherWithPermissionsGranted(view, viewModel)
                } else {
                    weatherMainConstraintContainer.visibility = View.GONE
                    lottieErrorAnimation.visibility = View.VISIBLE
                    Snackbar.make(
                        view,
                        getString(R.string.network_error_message),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                weatherSwipeRefreshLayout.isRefreshing = false
            }

            val weatherObjectObserver = Observer<com.appsflow.theweather.domain.models.WeatherInfo> {
                val updatedDate = SimpleDateFormat(
                    "dd/MM/yyyy", Locale.getDefault()
                ).format(Date(it.deltaTime.toLong() * 1000))
                val updatedTime = SimpleDateFormat(
                    "hh:mm a", Locale.getDefault()
                ).format(Date(it.deltaTime.toLong() * 1000))
                val temp = it.mainInfo.temp.toString().substringBefore(".")
                val minTemp = it.mainInfo.tempMin.toString().substringBefore(".")
                val maxTemp = it.mainInfo.tempMax.toString().substringBefore(".")

                tvLocation.text =
                    getString(R.string.tvLocation_text, it.cityName, it.systemInfo.country)
                tvUpdatedAt.text = getString(R.string.tvUpdatedAt_text, updatedDate, updatedTime)
                tvCurrentWeatherStatus.text =
                    it.weatherDescription[0].description.replaceFirstChar { it.uppercase() }
                tvTemperature.text = getString(R.string.formatted_temperature_text, temp)
                tvMinTemp.text = getString(R.string.formatted_temperature_text, minTemp)
                tvMaxTemp.text = getString(R.string.formatted_temperature_text, maxTemp)
                tvPressure.text = it.mainInfo.pressure.toString()
                tvHumidity.text = it.mainInfo.humidity.toString()
                tvWind.text = it.windInfo.speed.toString()

                tvSunrise.text = SimpleDateFormat(
                    "hh:mm a", Locale.getDefault()
                ).format(Date(it.systemInfo.sunrise.toLong() * 1000))

                tvSunset.text = SimpleDateFormat(
                    "hh:mm a", Locale.getDefault()
                ).format(Date(it.systemInfo.sunset.toLong() * 1000))

                val animation: Animation =
                    AnimationUtils.loadAnimation(requireContext(), R.anim.fade_fragment)
                weatherMainConstraintContainer.startAnimation(animation)
            }

            viewModel.weatherObject.observe(requireActivity(), weatherObjectObserver)

            btnForecast.setOnClickListener {
                val action = WeatherFragmentDirections.actionWeatherFragmentToForecastFragment()
                findNavController().navigate(action)
            }

            viewModel.loading.observe(requireActivity(), {
                if (it) {
                    weatherMainConstraintContainer.visibility = View.GONE
                    lottieLoadingAnimation.visibility = View.VISIBLE
                } else {
                    lottieLoadingAnimation.visibility = View.GONE
                    weatherMainConstraintContainer.visibility = View.VISIBLE
                }
            })

            viewModel.errorMessage.observe(requireActivity(), {
                Snackbar.make(view, it, Snackbar.LENGTH_SHORT).show()
            })
        }
    }

    private fun getWeatherWithPermissionsGranted(view: View, viewModelImpl: WeatherViewModelImpl) {
        when {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                val locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        locationResult ?: return
                    }
                }
                val locationRequest = LocationRequest.create().apply {
                    priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
                }
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
                fusedLocationClient.lastLocation
                    .addOnSuccessListener {
                        viewModelImpl.getWeatherResponse(
                            lat = it.latitude.toString(),
                            lon = it.longitude.toString(),
                            units = "metric"
                        )
                    }
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                Snackbar.make(
                    view,
                    getString(R.string.location_permission_rationale),
                    Snackbar.LENGTH_LONG
                ).setAction(
                    getString(R.string.grant_button)
                ) {
                    requestPermissionLauncher
                        .launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    getWeatherWithPermissionsGranted(view, viewModelImpl)
                }
                    .show()
            }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                getWeatherWithPermissionsGranted(view, viewModelImpl)
            }
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}