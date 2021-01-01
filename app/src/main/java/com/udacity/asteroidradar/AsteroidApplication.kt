package com.udacity.asteroidradar

import android.app.Application
import androidx.work.*
import com.udacity.asteroidradar.workmanager.FetchWorker
import java.util.concurrent.TimeUnit

class AsteroidApplication :Application() {

    override fun onCreate() {
        super.onCreate()
        initWorkRequests()
    }

    private fun initWorkRequests(){
        val workManager = WorkManager.getInstance()
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val periodicRequest = PeriodicWorkRequestBuilder<FetchWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()
        workManager.enqueueUniquePeriodicWork("FetchAsteroidsPeriodic", ExistingPeriodicWorkPolicy.KEEP, periodicRequest)
    }
}