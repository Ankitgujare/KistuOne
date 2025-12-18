package com.example.kitsuone

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.kitsuone.di.AppContainer
import com.example.kitsuone.di.DefaultAppContainer
import com.example.kitsuone.util.NotificationUtils
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KitsuOneApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        NotificationUtils.createNotificationChannel(this)
    }
}
