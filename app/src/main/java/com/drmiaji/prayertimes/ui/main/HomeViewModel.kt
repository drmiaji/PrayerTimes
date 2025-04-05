package com.drmiaji.prayertimes.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.media.MediaPlayer
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import com.drmiaji.prayertimes.data.model.Prayer
import com.drmiaji.prayertimes.data.model.PrayerReminder
import com.drmiaji.prayertimes.data.model.Schedule
import com.drmiaji.prayertimes.data.model.TimingSchedule
import com.drmiaji.prayertimes.data.model.getScheduleName
import com.drmiaji.prayertimes.repo.PrayerRepository
import com.drmiaji.prayertimes.repo.States
import com.drmiaji.prayertimes.service.PrayerAlarm
import com.drmiaji.prayertimes.R
import com.drmiaji.prayertimes.data.model.hour
import com.drmiaji.prayertimes.data.model.minutes
import com.drmiaji.prayertimes.data.model.toList
import com.drmiaji.prayertimes.data.model.toTimingSchedule
import com.drmiaji.prayertimes.utils.TimeUtils.day
import com.drmiaji.prayertimes.utils.TimeUtils.hour
import com.drmiaji.prayertimes.utils.TimeUtils.month
import com.drmiaji.prayertimes.utils.TimeUtils.year
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: PrayerRepository,
    private val prayerAlarm: PrayerAlarm
) : ViewModel() {

    var currentScheduleC = MutableStateFlow(Schedule.EMPTY)
    var currentSchedule by mutableStateOf(Schedule.EMPTY)

    var timingScheduleC = MutableStateFlow(TimingSchedule.EMPTY)
    var timingSchedule by mutableStateOf(TimingSchedule.EMPTY)
    private var currentTimingSchedule = TimingSchedule.EMPTY
    var isLoading by mutableStateOf(true)

    var nextPray by mutableStateOf("-")
    var descNextPray by mutableStateOf("-")
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var appContext: Context

    var locationAddress: String = ""
        private set

    private lateinit var mediaPlayer: MediaPlayer

    fun getPrayerSchedule(lat: Double, long: Double, date: Timestamp) = viewModelScope.launch {
        repository.getSchedule(lat, long, date.month, date.year).collect {
            when (it) {
                is States.Loading -> {
                    Log.d("TAG", "getPrayerSchedule: loading")
                    isLoading = true
                }

                is States.Success -> {
                    isLoading = false
                    it.data.find { sc ->
                        sc.georgianDate.day == if (date.hour > 20) date.day + 1 else date.day
                    }?.let { schedule ->
                        viewModelScope.launch {
                            currentScheduleC.emit(schedule)
                            currentSchedule = schedule
                        }
                        getReminderPrayer(schedule.timingSchedule)
                        viewModelScope.launch {
                            timingSchedule = currentTimingSchedule
                            timingScheduleC.emit(currentTimingSchedule)
                        }
                    }
                }

                is States.Failed -> {
                    isLoading = false
                    Log.d("TAG", "getPrayerSchedule: failed = ${it.message}")
                }
            }
        }
    }

    private fun getReminderPrayer(timingSchedule: TimingSchedule) = viewModelScope.launch {
        repository.getAllReminder().collect {
            if (it.isEmpty()) repository.addAllReminders(PrayerReminder.EMPTY)
            else updateReminder(it, timingSchedule)
        }
    }

    private fun updateReminder(
        listOfReminder: List<PrayerReminder>,
        timingSchedule: TimingSchedule
    ) {
        val listSchedule = timingSchedule.toList()
        if (listSchedule.isNotEmpty()) {
            listOfReminder.forEach { reminder ->
                if (reminder.index in listSchedule.indices) { // ✅ prevent out-of-bounds crash
                    listSchedule[reminder.index].isReminded = reminder.isReminded
                }
            }
            currentTimingSchedule = listSchedule.toTimingSchedule()
            viewModelScope.launch { this@HomeViewModel.timingSchedule = TimingSchedule.EMPTY }
            viewModelScope.launch { this@HomeViewModel.timingSchedule = currentTimingSchedule }
        }
    }

    fun updatePrayer(
        context: Context,
        timingSchedule: TimingSchedule,
        prayerTime: String,
        isReminded: Boolean,
        position: Int
    ) = viewModelScope.launch {
        countDownTimer.onFinish()
        val prayerReminder = PrayerReminder(position, prayerTime, isReminded)
        if (isReminded) prayerAlarm.setPrayerAlarm(context, prayerReminder)
        else prayerAlarm.cancelAlarm(context, prayerReminder.index)
        viewModelScope.launch {
            repository.updateReminder(PrayerReminder(position, prayerTime, isReminded))
            getReminderPrayer(timingSchedule)
        }
    }

    fun getIntervalText(context: Context, timingSchedule: TimingSchedule, prayer: Prayer) = viewModelScope.launch {
        appContext = context
        val now = Timestamp.now()

        // Calculate the prayer time for today or tomorrow if needed
        val prayerCalendar = Calendar.getInstance().apply {
            set(
                Calendar.DAY_OF_MONTH,
                if (now.hour > timingSchedule.isha.time.hour) now.day + 1 else now.day
            )
            set(Calendar.HOUR_OF_DAY, prayer.time.hour)
            set(Calendar.MINUTE, prayer.time.minutes)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Calculate milliseconds until prayer time
        val currentTimeMillis = now.toDate().time
        val prayerTimeMillis = prayerCalendar.timeInMillis

        // Calculate the difference in milliseconds
        val diffMillis = prayerTimeMillis - currentTimeMillis

        if (this@HomeViewModel::countDownTimer.isInitialized) countDownTimer.cancel()

        countDownTimer = object : CountDownTimer(diffMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Convert to hours, minutes, seconds using proper division
                val seconds = (millisUntilFinished / 1000) % 60
                val minutes = (millisUntilFinished / (1000 * 60)) % 60
                val hours = millisUntilFinished / (1000 * 60 * 60)

                // Format using String.format with explicit locale for consistent display with leading zeros
                nextPray = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
                descNextPray = " to ${timingSchedule.getScheduleName(prayer)}"
            }

            override fun onFinish() {
                if (this@HomeViewModel::countDownTimer.isInitialized) countDownTimer.cancel()
                nextPray = "Now"
                descNextPray = " it's time to pray ${timingSchedule.getScheduleName(prayer)}"
                playAzan(appContext)
            }
        }.start()
    }

    private fun playAzan(context: Context) {
        mediaPlayer = MediaPlayer.create(context, R.raw.adzan_makkah)
        mediaPlayer.start()
    }

    @SuppressLint("MissingPermission")
    fun getLocationAddress(context: Context, location: Location) {
        viewModelScope.launch {
            val geocoder = Geocoder(context, Locale.getDefault())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Use the new API for Android 13+ (API 33+)
                geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1,
                    object : Geocoder.GeocodeListener {
                        override fun onGeocode(addresses: List<Address>) {
                            if (addresses.isNotEmpty()) {
                                processAddress(addresses.first())
                            }
                        }
                    }
                )
            } else {
                // Use the old API for older versions
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(location.latitude, location.longitude, 1)?.let { addresses ->
                    if (addresses.isNotEmpty()) {
                        processAddress(addresses.first())
                    }
                }
            }
        }
    }
    private fun processAddress(address: Address) {
        locationAddress = buildString {
            append(address.getAddressLine(0))
        }
    }
}