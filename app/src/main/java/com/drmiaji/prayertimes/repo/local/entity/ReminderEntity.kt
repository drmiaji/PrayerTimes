package com.drmiaji.prayertimes.repo.local.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.drmiaji.prayertimes.data.model.PrayerReminder

@Entity(tableName = "reminder")
data class ReminderEntity(
    @PrimaryKey
    @ColumnInfo(name = "index")
    val index: Int,
    @ColumnInfo(name = "time")
    val time: String,
    @ColumnInfo(name = "reminded")
    var isReminded: Boolean
){
    companion object{
        val INIT = listOf(
            ReminderEntity(0, "-", false),
            ReminderEntity(1, "-", false),
            ReminderEntity(2, "-", false),
            ReminderEntity(3, "-", false),
            ReminderEntity(4, "-", false),
            ReminderEntity(5, "-", false),
            ReminderEntity(6, "-", false),
        )
    }
}

fun List<ReminderEntity>.toPayerReminders(): List<PrayerReminder> {
    val listOfPrayerReminder = mutableListOf<PrayerReminder>()
    this.forEach {
        listOfPrayerReminder.add(PrayerReminder(it.index, it.time, it.isReminded))
    }
    return listOfPrayerReminder
}