package app.ynemreuslu.prayertimes.ui.home

import app.ynemreuslu.prayertimes.domain.prayer.PrayerTimings

object HomeContract {
    data class UiState(
        val isLoading: Boolean = false,
        val prayerTimings: PrayerTimings? = null,
        val errorPrayerTimings: String? = null,
        val location: String? = null,
        val latitude: Double? = null,
        val longitude: Double? = null,
        val gregorianDate: String? = null,
        val hijriDate: String? = null,
        val prayerIndex: Int? = null,
        val prayerHours: String? = null,
        val prayerMinutes: String? = null,
        val prayerSeconds: String? = null,
    )

    sealed class UiAction

    sealed class UiEffect {
        data object RequestLocationPermission : UiEffect()
    }
}