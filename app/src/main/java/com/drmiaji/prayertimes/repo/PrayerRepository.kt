package com.drmiaji.prayertimes.repo

import com.drmiaji.prayertimes.data.model.*
import com.drmiaji.prayertimes.data.repository.DataRepositoryImpl
import com.drmiaji.prayertimes.repo.local.LocalDataSource
import com.drmiaji.prayertimes.repo.local.entity.ProgressTaskEntity
import com.drmiaji.prayertimes.repo.local.entity.toPayerReminders
import com.drmiaji.prayertimes.repo.local.entity.toProgressTask
import com.drmiaji.prayertimes.repo.remote.RemoteDataSource
import com.drmiaji.prayertimes.utils.TimeUtils.indexOfDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PrayerRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    internal val localDataSource: LocalDataSource
) : DataRepositoryImpl {
    override suspend fun getSchedule(
        lat: Double, long: Double, month: Int, year: Int
    ): Flow<States<List<Schedule>>> = remoteDataSource.getSchedule(lat, long, month, year)

    override suspend fun getAllReminder(): Flow<List<PrayerReminder>> =
        localDataSource.getAllReminder().map { it.toPayerReminders() }

    override suspend fun addAllReminders(listOfReminder: List<PrayerReminder>) =
        localDataSource.addAllReminder(listOfReminder.toReminderEntities())

    override suspend fun updateReminder(prayerReminder: PrayerReminder) =
        localDataSource.updateReminder(prayerReminder.toReminderEntity())

    override suspend fun deleteAllReminder() = localDataSource.deleteAllReminder()

    override suspend fun getProgressTask(date: String): Flow<List<ProgressTask>> = flow {
        localDataSource.getCheckedTask(date).collect { checkedTask ->
            localDataSource.getAllProgressTask().collect { entity ->
                emit(entity.filter { it.filterDay() }.sortedBy { it.dateLong }
                    .toProgressTask(checkedTask))
            }
        }
    }

    suspend fun getCheckedTask(date: String) = flow {
        localDataSource.getCheckedTask(date).collect { emit(it) }
    }

    suspend fun addCheckedTask(task: ProgressTask, onFinish: () -> Unit) {
        localDataSource.addCheckedTask(task.toCheckedEntity())
        onFinish()
    }

    private fun ProgressTaskEntity.filterDay(): Boolean {
        this.repeating.split(" ").forEach { part ->
            val day = part.toIntOrNull()
            if (day != null && listOf(indexOfDay, 7, -1).contains(day)) {
                return true
            }
        }
        return false
    }

    override suspend fun addProgressTask(task: ProgressTask) {
        localDataSource.addProgressTask(task.toProgressEntity())
        localDataSource.addCheckedTask(task.toCheckedEntity())
    }

    override suspend fun deleteProgressTask(task: ProgressTask) {
        localDataSource.deleteProgressTask(task.toProgressEntity())
        localDataSource.deleteCheckedTask(task.toCheckedEntity())
    }

    override suspend fun updateCheckedTask(task: ProgressTask, onFinish: () -> Unit) {
        localDataSource.updateCheckedTask(task.toCheckedEntity())
        onFinish()
    }
}
