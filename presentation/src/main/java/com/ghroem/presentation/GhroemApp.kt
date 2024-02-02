package com.ghroem.presentation

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.ghroem.R
import com.ghroem.presentation.utils.MUSIC_CHANNEL_ID
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GhroemApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        /** Create the NotificationChannel, but only on API 26+ because
         *  the NotificationChannel class is new and not in the support library*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(MUSIC_CHANNEL_ID, name, importance).apply {
                setSound(null, null)
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
