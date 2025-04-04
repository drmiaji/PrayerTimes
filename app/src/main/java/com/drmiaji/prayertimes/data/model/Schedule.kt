package com.drmiaji.prayertimes.data.model

import com.google.firebase.Timestamp
import com.drmiaji.prayertimes.repo.remote.response.ScheduleResponse
import com.drmiaji.prayertimes.utils.TimeUtils.hour
import com.drmiaji.prayertimes.utils.TimeUtils.minutes
import kotlin.text.compareTo

data class Schedule(
    val timingSchedule: TimingSchedule,
    val georgianDate: DateSchedule,
    val hijriDate: DateSchedule,
    val metaSchedule: MetaSchedule
) {
    companion object {
        val EMPTY = ScheduleResponse().toSchedule()
    }
}

data class MetaSchedule(
    val latitude: Double,
    val longitude: Double,
    val timeZone: String
)

data class DateSchedule(
    val day: Int,
    val month: Int,
    val monthDesignation: String,
    val year: Int,
    val yearDesignation: String,
    val weekday: String,
    val date: String,
    val holidays: List<String>
)

data class TimingSchedule(
   // val imsak: Prayer,
    val fajr: Prayer,
    val sunrise: Prayer,
    val dhuhr: Prayer,
    val asr: Prayer,
    val maghrib: Prayer,
    val isha: Prayer
) {
    companion object {
        val EMPTY = TimingSchedule(
         //   Prayer.EMPTY,
            Prayer.EMPTY,
            Prayer.EMPTY,
            Prayer.EMPTY,
            Prayer.EMPTY,
            Prayer.EMPTY,
            Prayer.EMPTY
        )
    }
}

data class Prayer(
    val time: String,
    var isReminded: Boolean
) {
    companion object {
        val EMPTY = Prayer("-", false)
    }
}

fun TimingSchedule.getScheduleName(time: Prayer): String {
    return when (this.toList().indexOf(time)) {
       // 0 -> "Imsak"
        0 -> "Fajr"
        1 -> "Sunrise"
        2 -> "Dhuhr"
        3 -> "Asr"
        4 -> "Maghrib"
        5 -> "Isha"
        else -> "-"
    }
}

fun TimingSchedule.getNearestSchedule(timestamp: Timestamp): Prayer =
    this.toList()
        .filter { it != this.sunrise && it.time.hour >= timestamp.hour }
        .firstOrNull {
            if (it.time.hour == timestamp.hour) it.time.minutes >= timestamp.minutes
            else it.time.hour >= timestamp.hour
        } ?: this.toList().filter { it != this.sunrise }.minByOrNull { it.time.hour } ?: Prayer.EMPTY

fun TimingSchedule.toList() = if (this.fajr.time != "-") listOf(
    this.fajr, this.sunrise, this.dhuhr, this.asr, this.maghrib, this.isha,
) else listOf()

fun List<Prayer>.toTimingSchedule() =
    TimingSchedule(this[0], this[1], this[2], this[3], this[4], this[5])

// ✅ UPDATED:
val String.onlyTime: String
    get() = this.split(" ").first()

val String.hour: Int
    get() = if (this != "-" && this.contains(":")) this.onlyTime.split(":")[0].toInt() else 0

val String.minutes: Int
    get() = if (this != "-" && this.contains(":")) this.onlyTime.split(":")[1].toInt() else 0

fun List<ScheduleResponse>.toSchedule(): MutableList<Schedule> {
    val listOfSchedule = mutableListOf<Schedule>()
    this.forEach { listOfSchedule.add(it.toSchedule()) }
    return listOfSchedule
}

fun String.addMinutes(minutes: Int): String {
    return try {
        val formatter = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        val date = formatter.parse(this)
        val newTime = java.util.Date(date!!.time + minutes * 60 * 1000)
        formatter.format(newTime)
    } catch (_: Exception) {
        this // fallback if parsing fails
    }
}

fun ScheduleResponse.toSchedule(): Schedule {
    return Schedule(
        timingSchedule = TimingSchedule(
            // imsak = Prayer((this.timingResponse?.imsak ?: "-").addMinutes(0), false),
            fajr = Prayer((this.timingResponse?.fajr ?: "-").addMinutes(18), false),
            sunrise = Prayer((this.timingResponse?.sunrise ?: "-").addMinutes(0), false),
            dhuhr = Prayer((this.timingResponse?.dhuhr ?: "-").addMinutes(0), false),
            asr = Prayer((this.timingResponse?.asr ?: "-").addMinutes(0), false),
            maghrib = Prayer((this.timingResponse?.maghrib ?: "-").addMinutes(4), false),
            isha = Prayer((this.timingResponse?.isha ?: "-").addMinutes(-19), false),
        ),
        georgianDate = DateSchedule(
            day = (this.dateResponse?.gregorian?.day ?: "0").toInt(),
            month = this.dateResponse?.gregorian?.monthResponse?.number ?: 0,
            monthDesignation = this.dateResponse?.gregorian?.monthResponse?.en ?: "",
            year = (this.dateResponse?.gregorian?.year ?: "0").toInt(),
            yearDesignation = "AD",
            weekday = this.dateResponse?.gregorian?.weekdayResponse?.en ?: "",
            date = this.dateResponse?.gregorian?.date ?: "",
            holidays = this.dateResponse?.gregorian?.holidays ?: listOf()
        ),
        hijriDate = DateSchedule(
            day = (this.dateResponse?.hijri?.day ?: "0").toInt(),
            month = this.dateResponse?.hijri?.monthResponse?.number ?: 0,
            monthDesignation = this.dateResponse?.hijri?.monthResponse?.en ?: "",
            year = (this.dateResponse?.hijri?.year ?: "0").toInt(),
            yearDesignation = "AH",
            weekday = this.dateResponse?.hijri?.weekdayResponse?.en ?: "",
            date = this.dateResponse?.hijri?.date ?: "",
            holidays = this.dateResponse?.hijri?.holidays ?: listOf()
        ),
        metaSchedule = MetaSchedule(
            latitude = this.metaResponse?.latitude ?: 0.0,
            longitude = this.metaResponse?.longitude ?: 0.0,
            timeZone = this.metaResponse?.timezone ?: ""
        )
    )
}
