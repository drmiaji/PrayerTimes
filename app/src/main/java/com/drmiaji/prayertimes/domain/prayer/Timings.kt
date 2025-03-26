package com.drmiaji.prayertimes.domain.prayer



data class Timings(
    val fajrTime: String,
    val sunriseTime: String,
    val dhuhrTime: String,
    val asrTime: String,
    val sunsetTime: String,
    val maghribTime: String,
    val ishaTime: String,
    val midnightTime: String,
    val firstThirdTime: String,
    val lastThirdTime: String
)

