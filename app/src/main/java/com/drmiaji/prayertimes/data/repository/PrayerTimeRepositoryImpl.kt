package com.drmiaji.prayertimes.data.repository

import com.drmiaji.prayertimes.data.mappers.toPrayerTimings
import com.drmiaji.prayertimes.data.source.PrayerTimeApi
import com.drmiaji.prayertimes.domain.prayer.PrayerTimings
import com.drmiaji.prayertimes.domain.repository.PrayerTimeRepository
import javax.inject.Inject


class PrayerTimeRepositoryImpl @Inject constructor(private val prayerTimeApi: PrayerTimeApi) :
    PrayerTimeRepository {
    override suspend fun getPrayerTimesForDate(
        date: String,
        location: String,
        method: Int
    ): PrayerTimings {
        return prayerTimeApi.getPrayerTimesForDate(date, location, method).toPrayerTimings()
    }

}