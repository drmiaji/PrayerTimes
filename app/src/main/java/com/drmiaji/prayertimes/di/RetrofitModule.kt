package com.drmiaji.prayertimes.di


import com.drmiaji.prayertimes.common.Constants.BASE_URL
import com.drmiaji.prayertimes.data.source.PrayerTimeApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providePrayerTimeApi(retrofit: Retrofit): PrayerTimeApi {
        return retrofit.create(PrayerTimeApi::class.java)
    }
}
