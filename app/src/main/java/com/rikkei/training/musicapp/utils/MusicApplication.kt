package com.rikkei.training.musicapp.utils

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class MusicApplication: Application() {

    companion object{
        const val CHANNEL_ID = "channel_service"
        const val PLAY = "play"
        const val NEXT = "next"
        const val PREVIOUS = "previous"
        const val EXIT = "exit"
    }

    override fun onCreate() {
        super.onCreate()
        createChanelNotification()
    }

    private fun createChanelNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(CHANNEL_ID, "Now playing", NotificationManager.IMPORTANCE_HIGH)
            channel.setSound(null, null)
            channel.description = "This is important channel for showing song!!"
            val manager = getSystemService(NotificationManager::class.java) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    val database: ItemRoomDatabase by lazy { ItemRoomDatabase.getDatabase(this) }
}