package com.appsflow.theweather

import android.app.Application
import timber.log.Timber

class TheWeather : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}