package com.example.aiusage.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.aiusage.repository.UsageRepository
import com.example.aiusage.utils.UsageStatsHelper
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

class AppUsageWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    // Access Hilt dependencies inside WorkManager without complex custom Configuration.Provider setup
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WorkerEntryPoint {
        fun usageRepository(): UsageRepository
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            WorkerEntryPoint::class.java
        )
        val usageRepository = entryPoint.usageRepository()

        try {
            val calendar = Calendar.getInstance()
            val endTime = calendar.timeInMillis
            
            // Start of today (midnight)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startTime = calendar.timeInMillis

            // Fetch the updated usage stats
            val usageRecords = UsageStatsHelper.fetchUsageStats(applicationContext, startTime, endTime)

            if (usageRecords.isNotEmpty()) {
                // Clear any records already saved for today, and insert the updated set
                usageRepository.deleteUsageForDate(startTime)
                usageRepository.insertUsageRecords(usageRecords)
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
