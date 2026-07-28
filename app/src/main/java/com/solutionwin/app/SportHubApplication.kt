package com.solutionwin.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.solutionwin.app.notifications.ReminderReceiver
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SportHubApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ReminderReceiver.CHANNEL_ID,
                getString(R.string.notification_channel_events),
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = getString(R.string.notification_channel_events_description)
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }
}
