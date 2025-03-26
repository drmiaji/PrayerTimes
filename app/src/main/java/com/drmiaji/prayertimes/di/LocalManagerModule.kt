package com.drmiaji.prayertimes.di

import android.content.Context
import android.content.SharedPreferences
import android.hardware.SensorManager
import com.drmiaji.prayertimes.data.repository.LocalPermissionManagerImpl
import com.drmiaji.prayertimes.domain.repository.LocalPermissionManagerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalManagerModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences(
            "app_preferences",
            Context.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    fun provideLocalPermissionManagerRepository(
        sharedPreferences: SharedPreferences
    ): LocalPermissionManagerRepository {
        return LocalPermissionManagerImpl(sharedPreferences)
    }

    @Provides
    fun provideSensorManager(@ApplicationContext context: Context): SensorManager {
        return context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
}