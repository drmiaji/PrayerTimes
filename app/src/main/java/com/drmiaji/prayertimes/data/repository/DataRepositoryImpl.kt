package com.drmiaji.prayertimes.data.repository

import com.drmiaji.prayertimes.data.model.PrayerReminder
import com.drmiaji.prayertimes.data.model.ProgressTask
import com.drmiaji.prayertimes.data.model.Schedule
import com.drmiaji.prayertimes.repo.States
import kotlinx.coroutines.flow.Flow

interface DataRepositoryImpl {
    suspend fun getSchedule(lat: Double, long: Double, month: Int, year: Int): Flow<States<List<Schedule>>>

    suspend fun getAllReminder(): Flow<List<PrayerReminder>>
    suspend fun addAllReminders(listOfReminder: List<PrayerReminder>)
    suspend fun updateReminder(prayerReminder: PrayerReminder)
    suspend fun deleteAllReminder()

    suspend fun getProgressTask(date: String): Flow<List<ProgressTask>>
    suspend fun addProgressTask(task: ProgressTask)
    suspend fun deleteProgressTask(task: ProgressTask)

    suspend fun updateCheckedTask(task: ProgressTask, onFinish: () -> Unit)
}