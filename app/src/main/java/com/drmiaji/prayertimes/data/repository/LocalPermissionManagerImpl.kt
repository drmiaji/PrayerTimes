package com.drmiaji.prayertimes.data.repository


import android.content.SharedPreferences
import jakarta.inject.Inject
import com.drmiaji.prayertimes.domain.repository.LocalPermissionManagerRepository

class LocalPermissionManagerImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : LocalPermissionManagerRepository {

    override fun isLocationPermissionGranted(): Boolean {
        return sharedPreferences.getBoolean(KEY_LOCATION_PERMISSION_GRANTED, false)
    }

    override suspend fun setLocationPermissionGrantedStatus(isGranted: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_LOCATION_PERMISSION_GRANTED, isGranted).apply()
    }

    override  fun fetchLatitude(latitude: Double) {
        sharedPreferences.edit().putFloat(KEY_LATITUDE, latitude.toFloat()).apply()
    }

    override  fun getLatitude(): Double {
        return sharedPreferences.getFloat(KEY_LATITUDE, 0.0f).toDouble()
    }

    override  fun fetchLongitude(longitude: Double) {
        sharedPreferences.edit().putFloat(KEY_LONGITUDE, longitude.toFloat()).apply()
    }

    override  fun getLongitude(): Double {
        return sharedPreferences.getFloat(KEY_LONGITUDE, 0.0f).toDouble()
    }

    override suspend fun getLocationPermissionStatus(): String {
        return sharedPreferences.getString(KEY_LOCATION_PERMISSION_STATUS, "") ?: ""
    }

    override fun isGpsDisabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_GPS_DISABLED, false)
    }

    override suspend fun setGpsStatus(isDisabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_GPS_DISABLED, isDisabled).apply()
    }

    override suspend fun setLocationPermissionStatus(status: String) {
        sharedPreferences.edit().putString(KEY_LOCATION_PERMISSION_STATUS, status).apply()
    }

    override fun isNotificationPermissionGranted(): Boolean {
        return sharedPreferences.getBoolean(KEY_NOTIFICATION_PERMISSION_GRANTED, false)
    }

    override suspend fun setNotificationPermissionGrantedStatus(isGranted: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_NOTIFICATION_PERMISSION_GRANTED, isGranted).apply()
    }

    override suspend fun getNotificationPermissionDeniedCount(): Int {
        return sharedPreferences.getInt(KEY_NOTIFICATION_PERMISSION_DENIED_COUNT, 0)
    }

    override suspend fun incrementNotificationPermissionDeniedCount() {
        val currentCount = getNotificationPermissionDeniedCount()
        sharedPreferences.edit().putInt(KEY_NOTIFICATION_PERMISSION_DENIED_COUNT, currentCount + 1).apply()
    }

    override suspend fun resetNotificationPermissionDeniedCount() {
        sharedPreferences.edit().putInt(KEY_NOTIFICATION_PERMISSION_DENIED_COUNT, 0).apply()
    }

    override suspend fun getNotificationPermissionStatus(): String {
        return sharedPreferences.getString(KEY_NOTIFICATION_PERMISSION_STATUS, "") ?: ""
    }

    override suspend fun setNotificationPermissionStatus(status: String) {
        sharedPreferences.edit().putString(KEY_NOTIFICATION_PERMISSION_STATUS, status).apply()
    }

    override fun isSkipButtonEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_SKIP_BUTTON_ENABLED, false)
    }

    override suspend fun setSkipButtonState(isEnabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_SKIP_BUTTON_ENABLED, isEnabled).apply()
    }
    companion object {
        private const val KEY_LOCATION_PERMISSION_GRANTED = "location_permission_granted"
        private const val KEY_NOTIFICATION_PERMISSION_GRANTED = "notification_permission_granted"
        private const val KEY_SKIP_BUTTON_ENABLED = "skip_button_enabled"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
        private const val KEY_NOTIFICATION_PERMISSION_STATUS = "notification_permission_status"
        private const val KEY_LOCATION_PERMISSION_STATUS = "location_permission_status"
        private const val KEY_NOTIFICATION_PERMISSION_DENIED_COUNT = "notification_permission_denied_count"
        private const val KEY_GPS_DISABLED = "gps_disabled"
    }
}