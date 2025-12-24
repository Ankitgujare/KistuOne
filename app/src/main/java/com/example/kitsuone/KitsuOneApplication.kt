package com.example.kitsuone

import android.app.Application
import com.example.kitsuone.di.AppContainer
import com.example.kitsuone.di.DefaultAppContainer
import com.example.kitsuone.util.NotificationUtils
import dagger.hilt.android.HiltAndroidApp
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import com.example.kitsuone.worker.RecommendationWorker

@HiltAndroidApp
class KitsuOneApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        NotificationUtils.createNotificationChannel(this)
        setupWorker()
    }

    private fun setupWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<RecommendationWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "RecommendationWork",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
