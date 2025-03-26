package com.drmiaji.prayertimes.di

import com.drmiaji.prayertimes.data.repository.PrayerTimeRepositoryImpl
import com.drmiaji.prayertimes.domain.repository.PrayerTimeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPrayerTimeRepository(
        prayerTimeRepositoryImpl: PrayerTimeRepositoryImpl
    ): PrayerTimeRepository

}