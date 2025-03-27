package com.drmiaji.prayertimes.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.drmiaji.prayertimes.repo.PrayerRepository
import com.drmiaji.prayertimes.repo.local.LocalDataSource
import com.drmiaji.prayertimes.repo.local.room.AlifDatabase
import com.drmiaji.prayertimes.repo.local.room.ReminderDao
import com.drmiaji.prayertimes.repo.remote.RemoteDataSource
import com.drmiaji.prayertimes.repo.remote.network.PrayerService
import com.drmiaji.prayertimes.service.PrayerAlarm
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {

    @Provides
    fun provideRetrofitClient(): OkHttpClient {
        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()
    }

    @Provides
    fun providePrayerService(client: OkHttpClient): PrayerService {
        val retrofit = Retrofit.Builder().baseUrl("https://api.aladhan.com/v1/")
            .addConverterFactory(GsonConverterFactory.create()).client(client).build()
        return retrofit.create(PrayerService::class.java)
    }

    @Provides
    fun provideAlifDatabase(@ApplicationContext appContext: Context): AlifDatabase =
        AlifDatabase.getInstance(appContext)

    @Provides
    fun provideReminderDao(alifDatabase: AlifDatabase): ReminderDao = alifDatabase.reminderDao()

    @Provides
    fun provideLocalDataSource(reminderDao: ReminderDao) = LocalDataSource(reminderDao)

    @Provides
    fun providePrayerRepository(
        remoteDataSource: RemoteDataSource, localDataSource: LocalDataSource
    ): PrayerRepository = PrayerRepository(remoteDataSource, localDataSource)

    @Provides
    fun providePrayerAlarm(): PrayerAlarm = PrayerAlarm()
}