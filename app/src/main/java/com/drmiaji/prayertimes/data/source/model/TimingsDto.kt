package com.drmiaji.prayertimes.data.source.model

import com.google.gson.annotations.SerializedName

data class TimingsDto(
    @SerializedName("Fajr")
    val fajrTime: String,
    @SerializedName("Sunrise")
    val sunriseTime: String,
    @SerializedName("Dhuhr")
    val dhuhrTime: String,
    @SerializedName("Asr")
    val asrTime: String,
    @SerializedName("Sunset")
    val sunsetTime: String,
    @SerializedName("Maghrib")
    val maghribTime: String,
    @SerializedName("Isha")
    val ishaTime: String,
    @SerializedName("Midnight")
    val midnightTime: String,
    @SerializedName("Firstthird")
    val firstThirdTime: String,
    @SerializedName("Lastthird")
    val lastThirdTime: String
)
