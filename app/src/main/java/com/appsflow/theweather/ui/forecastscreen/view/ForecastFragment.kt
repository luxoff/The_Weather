package com.appsflow.theweather.ui.forecastscreen.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.appsflow.theweather.R
import com.appsflow.theweather.data.model.extra.forecast.Daily
import com.appsflow.theweather.data.service.MainRepository
import com.appsflow.theweather.data.service.network.WeatherAPI
import com.appsflow.theweather.databinding.FragmentForecastBinding
import com.appsflow.theweather.ui.forecastscreen.viewmodel.ForecastViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

class ForecastFragment : Fragment(R.layout.fragment_forecast) {
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var forecastListAdapter: ForecastListAdapter

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
        val binding = FragmentForecastBinding.bind(view)
        val weatherService = WeatherAPI.getWeatherService()
        val mainRepository = MainRepository(weatherService)
        val viewModel = ForecastViewModel(mainRepository)
        val forecastList = mutableListOf<Daily>()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        forecastListAdapter = ForecastListAdapter(forecastList)

        binding.apply {
            if (isNetworkAvailable(requireContext())) {
                lottieErrorAnimation.visibility = View.GONE
                getForecastWithPermissionRequest(view, viewModel)
            } else {
                llForecastMainContent.visibility = View.GONE
                lottieErrorAnimation.visibility = View.VISIBLE
                Snackbar.make(
                    view,
                    getString(R.string.network_error_message),
                    Snackbar.LENGTH_LONG
                ).show()
            }

            ibBackToTheWeatherFragment.setOnClickListener {
                findNavController().popBackStack()
            }

            recyclerView.adapter = forecastListAdapter
            recyclerView.layoutManager = LinearLayoutManager(requireActivity())

            viewModel.dailyForecast.observe(requireActivity(), {
                it.forEach { day ->
                    forecastList.add(day)
                    forecastListAdapter.notifyItemInserted(forecastList.size - 1)
                }
                val animation: Animation =
                    AnimationUtils.loadAnimation(requireContext(), R.anim.fade_fragment)
                forecastContainerRelativeLayout.startAnimation(animation)
            })

            viewModel.loading.observe(requireActivity(), {
                if (it) {
                    llForecastMainContent.visibility = View.GONE
                    lottieLoadingAnimation.visibility = View.VISIBLE
                } else {
                    lottieLoadingAnimation.visibility = View.GONE
                    llForecastMainContent.visibility = View.VISIBLE
                }
            })

            forecastSwipeRefreshLayout.setOnRefreshListener {
                if (isNetworkAvailable(requireContext())) {
                    lottieErrorAnimation.visibility = View.GONE
                    getForecastWithPermissionRequest(view, viewModel)
                } else {
                    llForecastMainContent.visibility = View.GONE
                    lottieErrorAnimation.visibility = View.VISIBLE
                    Snackbar.make(
                        view,
                        getString(R.string.network_error_message),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                forecastSwipeRefreshLayout.isRefreshing = false
            }
        }

    }

    private fun getForecastWithPermissionRequest(view: View, viewModel: ForecastViewModel) {
        when {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener {
                        viewModel.getForecastResponse(
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
            else -> false
        }
    }
}