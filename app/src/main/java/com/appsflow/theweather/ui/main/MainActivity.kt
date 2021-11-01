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

class MainActivity : AppCompatActivity() {
    private val remoteMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val title = intent?.extras?.getString("remoteMessageTitle")
            val messageBody = intent?.extras?.getString("remoteMessageBody")
            val dangerLevel = intent?.extras?.getString("dangerLevel")
            val dialog = AlertDialog.Builder(this@MainActivity)
                .setTitle(title)
                .setMessage(messageBody)
                .setPositiveButton("SHOW ME") { _, _ ->
                    if (dangerLevel != null) {
                        val action = WeatherAlertFragmentDirections
                            .actionGlobalWeatherAlertFragment(dangerLevel.toInt())
                        findNavController(R.id.nav_host_fragment).navigate(action)
                    }
                }
                .setNegativeButton("DISMISS") { dialog, _ -> dialog.dismiss() }
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

    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(remoteMessageReceiver)
    }
}