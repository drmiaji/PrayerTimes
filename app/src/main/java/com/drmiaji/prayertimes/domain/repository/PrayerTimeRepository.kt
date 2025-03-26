package com.drmiaji.prayertimes.domain.repository

import com.drmiaji.prayertimes.domain.prayer.PrayerTimings


interface PrayerTimeRepository {

    suspend fun getPrayerTimesForDate(
        date: String,
        location: String,
        method: Int
    ): PrayerTimings

}