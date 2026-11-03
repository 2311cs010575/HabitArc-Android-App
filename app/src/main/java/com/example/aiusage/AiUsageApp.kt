package com.example.aiusage

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.aiusage.worker.AppUsageWorker
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class AiUsageApp : Application() {
    override fun onCreate() {
        super.onCreate()
        scheduleAppUsageWorker()
    }

    private fun scheduleAppUsageWorker() {
        val workRequest = PeriodicWorkRequestBuilder<AppUsageWorker>(
            1, TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "AppUsageStatsCollection",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
