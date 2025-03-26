package com.drmiaji.prayertimes.domain.usescase


import com.drmiaji.prayertimes.domain.prayer.PrayerTimings
import com.drmiaji.prayertimes.domain.repository.PrayerTimeRepository
import com.drmiaji.prayertimes.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetPrayerTimingsUseCase @Inject constructor(
    private val prayerRepository: PrayerTimeRepository
) {
    suspend operator fun invoke(
        date: String,
        address: String,
        method: Int
    ): Flow<Resource<PrayerTimings>> = flow {
        try {
            val response = prayerRepository.getPrayerTimesForDate(date, address, method)
            emit(Resource.Success(data = response))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.message.toString()))
        }
    }
}

