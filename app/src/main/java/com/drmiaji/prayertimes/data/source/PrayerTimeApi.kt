package com.drmiaji.prayertimes.data.source

import com.drmiaji.prayertimes.data.source.model.PrayerTimingsDto
import retrofit2.http.GET
import retrofit2.http.Query

interface PrayerTimeApi {
    @GET("timingsByAddress")
    suspend fun getPrayerTimesForDate(
        @Query("date") date: String,
        @Query("address") location: String,
        @Query("method") calculationMethod: Int
    ): PrayerTimingsDto
}