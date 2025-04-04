package com.drmiaji.prayertimes.service

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import com.drmiaji.prayertimes.R
import com.drmiaji.prayertimes.data.model.PrayerReminder
import com.drmiaji.prayertimes.data.model.ProgressTask
import com.drmiaji.prayertimes.data.model.hour
import com.drmiaji.prayertimes.data.model.minutes
import com.drmiaji.prayertimes.repo.PrayerRepository
import com.drmiaji.prayertimes.ui.main.MainActivity
import com.drmiaji.prayertimes.utils.TimeUtils
import com.drmiaji.prayertimes.utils.TimeUtils.day
import com.drmiaji.prayertimes.utils.TimeUtils.hour
import com.drmiaji.prayertimes.utils.TimeUtils.hourMinutes
import com.drmiaji.prayertimes.utils.TimeUtils.minutes
import java.util.*
import javax.inject.Inject
import android.os.VibratorManager

@AndroidEntryPoint
class PrayerAlarm : BroadcastReceiver() {

    companion object {
        const val EXTRA_ALARM = "extra_alarm"
        const val EXTRA_ALARM_ACTIVITY = "extra_alarm_activity"

        const val NOTIFICATION_TITLE = "Prayer Reminder"
        const val NOTIFICATION_TITLE_ACTIVITY = "Task Reminder"
        const val NOTIFICATION_REQUEST_CODE = 102
        const val CHANNEL_ID = "Reminder"
        const val CHANNEL_NAME = "Daily Reminder"
        private fun getScheduleName(index: Int) = when (index) {
          //  0 -> "Imsak"
            0 -> "Farj"
            1 -> "Sunrise"
            2 -> "Dhuhr"
            3 -> "Asr"
            4 -> "Maghrib"
            5 -> "Isha"
            else -> "-"
        }
    }

    @Inject
    lateinit var repository: PrayerRepository

    @SuppressLint("ServiceCast")
    override fun onReceive(context: Context, intent: Intent) {
        // Handle activity alarms
        val progressTask = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.extras?.getParcelable(EXTRA_ALARM_ACTIVITY, ProgressTask::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.extras?.getParcelable(EXTRA_ALARM_ACTIVITY)
        }

        progressTask?.let {
            if (Timestamp(Date(it.date)).hour >= Timestamp.now().hour) {
                showAlarmNotification(
                    context, it.id.toInt(), NOTIFICATION_TITLE_ACTIVITY,
                    "Now it's time to do ${it.title}"
                )
            }
        }

        // Handle prayer reminders
        val prayerReminder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.extras?.getParcelable(EXTRA_ALARM, PrayerReminder::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.extras?.getParcelable(EXTRA_ALARM)
        }

        prayerReminder?.let { reminder ->
            val reminderHour = reminder.time.split(":").first().toInt()
            if (reminderHour >= Timestamp.now().hour) {
                // Show notification
                showAlarmNotification(context, reminder.index, NOTIFICATION_TITLE, buildString {
                    append("Now it's time for ")
                    append(getScheduleName(reminder.index))
                    append(" prayer at ")
                    append(reminder.time)
                })

                // 🔔 Add vibration before playing azan
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    // Use VibratorManager for Android 12+
                    val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                    val vibrator = vibratorManager.defaultVibrator
                    vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    // No need for SDK_INT >= M check since your min SDK is already >= 23
                    val vibrator = context.getSystemService(Vibrator::class.java)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(1000)
                    }
                }

                // ✅ Play Azan with fallback
                val azanResId = if (reminder.index == 0) { // Fajr
                    try {
                        R.raw.adzan_fajr
                    } catch (_: Exception) {
                        R.raw.adzan_makkah
                    }
                } else {
                    R.raw.adzan_makkah
                }

                val mediaPlayer = MediaPlayer.create(context, azanResId)
                mediaPlayer?.apply {
                    isLooping = false
                    start()
                    setOnCompletionListener {
                        it.release()
                    }
                }
            }
        }
    }

    fun setPrayerAlarm(
        context: Context,
        prayerReminder: PrayerReminder,
        showToast: Boolean? = true
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, PrayerAlarm::class.java)
        intent.putExtra(EXTRA_ALARM, prayerReminder)

        val triggerAtMillis = TimeUtils.getNextPrayerTimeMillis(
            prayerReminder.time.hour,
            prayerReminder.time.minutes
        )

        val pendingIntent = PendingIntent.getBroadcast(
            context, prayerReminder.index,
            intent, PendingIntent.FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                Log.w("PrayerAlarm", "Exact alarm permission not granted.")
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }

        if (showToast == true) Toast.makeText(
            context,
            "Reminder for ${getScheduleName(prayerReminder.index)} at ${prayerReminder.time} is set",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun setActivityAlarm(context: Context, progressTask: ProgressTask, showToast: Boolean?) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, PrayerAlarm::class.java)
        intent.putExtra(EXTRA_ALARM_ACTIVITY, progressTask)
        val calendar = Calendar.getInstance()
        val currentDate = Timestamp(Date(progressTask.date))

        val repeating = progressTask.repeating.split(" ")
        if (repeating.size != 1) repeating.forEach {
            if (it.isNotBlank()) {
                // set multiple time and interval 7 and [id + interval]
                var interval = it.toInt() - TimeUtils.indexOfDay
                if (interval == -1) interval = 7

                calendar.apply {
                    set(Calendar.DAY_OF_MONTH, currentDate.day + interval)
                    set(Calendar.HOUR_OF_DAY, currentDate.hour)
                    set(Calendar.MINUTE, currentDate.minutes)
                    set(Calendar.SECOND, 0)
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context, (progressTask.id + interval).toInt(),
                    intent, PendingIntent.FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
                )
                val triggerAtMillis = calendar.timeInMillis

                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } else {
            calendar.apply {
                set(Calendar.HOUR_OF_DAY, currentDate.hour)
                set(Calendar.MINUTE, currentDate.minutes)
                set(Calendar.SECOND, 0)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, progressTask.id.toInt(),
                intent, PendingIntent.FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
            )
            when (repeating.first().toInt()) {
                7 -> alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
                else -> alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        }
        if (showToast == true) Toast.makeText(
            context,
            "Reminder for ${progressTask.title} at ${currentDate.hourMinutes} is set",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun cancelActivityAlarm(context: Context, progressTask: ProgressTask) {
        val repeating = progressTask.repeating.split(" ")
        if (repeating.size != 1) {
            repeating.forEach {
                if (it.isNotBlank()) {
                    var interval = it.toInt() - TimeUtils.indexOfDay
                    if (interval == -1) interval = 7
                    cancelAlarm(context, (progressTask.id + interval).toInt())
                }
            }
        } else cancelAlarm(context, progressTask.id.toInt())
        Toast.makeText(context, "Reminder for ${progressTask.title} is unset", Toast.LENGTH_SHORT)
            .show()
    }

    fun cancelAlarm(context: Context, id: Int, message: String? = "") {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, PrayerAlarm::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
        )
        pendingIntent.cancel()
        alarmManager.cancel(pendingIntent)
        message?.let {
            if (it.isNotBlank()) {
                val text = "Reminder for ${getScheduleName(id)} pray is unset"
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAlarmNotification(
        context: Context,
        id: Int,
        title: String,
        content: String
    ) {

        val notificationManagerCompat =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pendingIntent = PendingIntent.getActivity(
            context, NOTIFICATION_REQUEST_CODE, intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) FLAG_IMMUTABLE
            else PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alif)
            .setContentTitle(title)
            .setContentText(content)
            .setColor(ContextCompat.getColor(context, R.color.primary))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableVibration(true)
                vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            }

            builder.setChannelId(CHANNEL_ID)
            notificationManagerCompat.createNotificationChannel(channel)
        }

        val notification = builder.build()
        notificationManagerCompat.notify(id, notification)
    }
}