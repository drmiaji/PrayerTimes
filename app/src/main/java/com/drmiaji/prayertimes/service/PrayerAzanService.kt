package com.drmiaji.prayertimes.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.drmiaji.prayertimes.R

class PrayerAzanService : Service() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate() {
        super.onCreate()
        startAzan()
    }

    private fun startAzan() {
        val notification = createNotification()

        startForeground(1, notification)

        mediaPlayer = MediaPlayer.create(this, R.raw.adzan_makkah)
        mediaPlayer?.apply {
            isLooping = false
            start()
            setOnCompletionListener {
                stopSelf()
            }
        }
    }

    private fun createNotification(): Notification {
        val channelId = "azan_channel"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Azan Notification",
                NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Prayer Time")
            .setContentText("Playing Azan...")
            .setSmallIcon(R.drawable.ic_alif)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onBind(intent: Intent?): IBinder? = null
}