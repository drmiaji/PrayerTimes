package com.drmiaji.prayertimes.domain.repository


interface LocalPermissionManagerRepository {

    // Location Permission
    fun isLocationPermissionGranted(): Boolean
    suspend fun setLocationPermissionGrantedStatus(isGranted: Boolean)
     fun fetchLatitude(latitude: Double)
     fun getLatitude() : Double
     fun fetchLongitude(longitude: Double)
     fun getLongitude() : Double
    suspend fun getLocationPermissionStatus(): String
    fun isGpsDisabled(): Boolean
    suspend fun setGpsStatus(isDisabled: Boolean)
    suspend fun setLocationPermissionStatus(status: String)

    // Notification Permission
    fun isNotificationPermissionGranted(): Boolean
    suspend fun setNotificationPermissionGrantedStatus(isGranted: Boolean)
    suspend fun getNotificationPermissionStatus(): String
    suspend fun setNotificationPermissionStatus(status: String)
    suspend fun getNotificationPermissionDeniedCount(): Int
    suspend fun incrementNotificationPermissionDeniedCount()
    suspend fun resetNotificationPermissionDeniedCount()

    // Skip Button State
    fun isSkipButtonEnabled(): Boolean
    suspend fun setSkipButtonState(isEnabled: Boolean)
}
