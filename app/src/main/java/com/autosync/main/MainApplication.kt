package com.autosync.main

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

import com.autosync.main.notification.NotificationHelper

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
    }
}
