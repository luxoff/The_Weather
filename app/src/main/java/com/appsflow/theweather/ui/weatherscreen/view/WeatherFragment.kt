package com.appsflow.theweather.ui.weatherscreen.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.appsflow.theweather.R
import com.appsflow.theweather.data.model.WeatherInfo
import com.appsflow.theweather.data.service.MainRepository
import com.appsflow.theweather.data.service.network.WeatherAPI
import com.appsflow.theweather.databinding.FragmentWeatherBinding
import com.appsflow.theweather.ui.weatherscreen.viewmodel.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
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
                    Log.i("Permission: ", "Granted")
                } else {
                    Log.i("Permission: ", "Denied")
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentWeatherBinding.bind(view)
        val weatherService = WeatherAPI.getWeatherService()
        val mainRepository = MainRepository(weatherService)
        val viewModel = WeatherViewModel(mainRepository)
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
                    "Some error occurred! Check your internet connection and try again.",
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
                        "Some error occurred! Check your internet connection and try again.",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                weatherSwipeRefreshLayout.isRefreshing = false
            }

            val weatherObjectObserver = Observer<WeatherInfo> {
                tvLocation.text = "${it.cityName}, ${it.systemInfo.country}"
                tvUpdatedAt.text = "Updated ${
                    SimpleDateFormat(
                        "dd/MM/yyyy 'at' hh:mm a", Locale.ENGLISH
                    ).format(Date(it.deltaTime.toLong() * 1000))
                }"
                tvCurrentWeatherStatus.text =
                    it.weatherDescription[0].description.replaceFirstChar { it.uppercase() }
                tvTemperature.text = it.mainInfo.temp.toString().substringBefore(".") + "°C"
                tvMinTemp.text = it.mainInfo.tempMin.toString().substringBefore(".") + "°C"
                tvMaxTemp.text = it.mainInfo.tempMax.toString().substringBefore(".") + "°C"
                tvPressure.text = it.mainInfo.pressure.toString()
                tvHumidity.text = it.mainInfo.humidity.toString()
                tvWind.text = it.windInfo.speed.toString()

                tvSunrise.text = SimpleDateFormat(
                    "hh:mm a", Locale.ENGLISH
                ).format(Date(it.systemInfo.sunrise.toLong() * 1000))

                tvSunset.text = SimpleDateFormat(
                    "hh:mm a", Locale.ENGLISH
                ).format(Date(it.systemInfo.sunset.toLong() * 1000))

                val animation: Animation =
                    AnimationUtils.loadAnimation(requireContext(), R.anim.fade_temp)
                tvTemperature.startAnimation(animation)
            }

            viewModel.weatherObject.observe(requireActivity(), weatherObjectObserver)

            btnForecast.setOnClickListener {
                val action = WeatherFragmentDirections.actionWeatherFragmentToForecastFragment()
                findNavController().navigate(action)
            }

            viewModel.loading.observe(requireActivity(), {
                if(it){
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

    private fun getWeatherWithPermissionsGranted(view: View, viewModel: WeatherViewModel) {
        when {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener {
                        viewModel.getWeatherResponse(
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
                    "GRANT"
                ) {
                    requestPermissionLauncher
                        .launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
                    .show()
            }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
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
            /*//for other device how are able to connect with Ethernet
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            //for check internet over Bluetooth
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true*/
            else -> false
        }
    }

}