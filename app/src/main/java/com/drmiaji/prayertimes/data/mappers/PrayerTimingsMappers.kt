package com.drmiaji.prayertimes.data.mappers

import com.drmiaji.prayertimes.data.source.model.PrayerDataDto
import com.drmiaji.prayertimes.data.source.model.PrayerTimingsDto
import com.drmiaji.prayertimes.data.source.model.TimingsDto
import com.drmiaji.prayertimes.domain.prayer.PrayerData
import com.drmiaji.prayertimes.domain.prayer.PrayerTimings
import com.drmiaji.prayertimes.domain.prayer.Timings


fun TimingsDto.toTimings(): Timings {
    return Timings(
        fajrTime = this.fajrTime,
        sunriseTime = this.sunriseTime,
        dhuhrTime = this.dhuhrTime,
        asrTime = this.asrTime,
        sunsetTime = this.sunsetTime,
        maghribTime = this.maghribTime,
        ishaTime = this.ishaTime,
        midnightTime = this.midnightTime,
        firstThirdTime = this.firstThirdTime,
        lastThirdTime = this.lastThirdTime
    )
}

fun PrayerDataDto.toPrayerData(): PrayerData {
    return PrayerData(
        timings = this.timings.toTimings()
    )
}

fun PrayerTimingsDto.toPrayerTimings(): PrayerTimings {
    return PrayerTimings(
        code = this.code,
        status = this.status,
        data = this.data.toPrayerData()
    )
}