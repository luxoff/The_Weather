package com.appsflow.theweather.ui.main

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import com.appsflow.theweather.R
import com.appsflow.theweather.ui.weatheralertscreen.WeatherAlertFragmentDirections
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

class MainActivity : AppCompatActivity() {
    private val remoteMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val title = intent?.extras?.getString("remoteMessageTitle")
            val messageBody = intent?.extras?.getString("remoteMessageBody")
            val dangerLevel = intent?.extras?.getString("dangerLevel")
            val dialog = AlertDialog.Builder(this@MainActivity)
                .setTitle(title)
                .setMessage(messageBody)
                .setPositiveButton(R.string.show_me) { _, _ ->
                    if (dangerLevel != null) {
                        val action = WeatherAlertFragmentDirections
                            .actionGlobalWeatherAlertFragment(dangerLevel.toInt())
                        findNavController(R.id.nav_host_fragment).navigate(action)
                    }
                }
                .setNegativeButton(R.string.dismiss) { dialog, _ -> dialog.dismiss() }
                .create()
            dialog.show()
        }
    }

    override fun onStart() {
        super.onStart()

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(remoteMessageReceiver, IntentFilter("PushNotification"))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val dialogGoogleServicesUnavailable = AlertDialog.Builder(this)
            .setTitle(R.string.gservices_unavailable_title)
            .setMessage(R.string.gservices_unavailable_message)
            .setPositiveButton(R.string.i_understand) { dialog, _ -> dialog.dismiss() }
            .create()
        if (googleApiAvailability.isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS) {
            dialogGoogleServicesUnavailable.show()
        }
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(remoteMessageReceiver)
    }
}