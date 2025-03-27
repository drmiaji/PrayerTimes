package com.drmiaji.prayertimes.data.model

import android.os.Parcelable
import com.drmiaji.prayertimes.repo.local.entity.ReminderEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class PrayerReminder(
    val index: Int,
    val time: String,
    var isReminded: Boolean
) : Parcelable {
    companion object{
        val EMPTY = listOf(
            PrayerReminder(0, "-", false), // fajr
            PrayerReminder(1, "-", false), // sunrise
            PrayerReminder(2, "-", false), // dhuhr
            PrayerReminder(3, "-", false), // asr
            PrayerReminder(4, "-", false), // maghrib
            PrayerReminder(5, "-", false), // isha
        )
    }
}

fun List<PrayerReminder>.toReminderEntities(): List<ReminderEntity> {
    val listOfEntity = mutableListOf<ReminderEntity>()
    this.forEach { listOfEntity.add(it.toReminderEntity()) }
    return listOfEntity
}

fun PrayerReminder.toReminderEntity(): ReminderEntity =
    ReminderEntity(this.index, this.time, this.isReminded)