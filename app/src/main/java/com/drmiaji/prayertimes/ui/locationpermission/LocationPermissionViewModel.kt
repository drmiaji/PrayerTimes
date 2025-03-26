package com.drmiaji.prayertimes.ui.locationpermission


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.IntentSenderRequest
import com.drmiaji.prayertimes.common.GpsStatusFlow
import com.drmiaji.prayertimes.common.hasLocationPermissionGranted
import com.drmiaji.prayertimes.domain.usescase.GpsControllerUseCase
import com.drmiaji.prayertimes.domain.usescase.LocationPermissionUseCase
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class LocationPermissionViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val gpsStatus: GpsStatusFlow,
    private val gpsControllerUseCase: GpsControllerUseCase,
    private val locationPermissionUseCase: LocationPermissionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationPermissionContract.UiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect by lazy { Channel<LocationPermissionContract.UiEffect>() }
    val uiEffect: Flow<LocationPermissionContract.UiEffect> by lazy { _uiEffect.receiveAsFlow() }


    fun onAction(action: LocationPermissionContract.UiAction) {
        when (action) {
            LocationPermissionContract.UiAction.OpenAppSettings -> navigateToAppSettings()
            LocationPermissionContract.UiAction.OnPermissionGranted -> startGpsMonitoring()
            LocationPermissionContract.UiAction.RequestPermission -> checkLocationPermissionStatus()
        }
    }

    private fun checkLocationPermissionStatus() {
        viewModelScope.launch {
            val hasLocationPermission = checkLocationPermissions()
            updateUiState { copy(isPermissionGranted = hasLocationPermission) }
            locationPermissionUseCase.invoke(hasLocationPermission)
            requestLocationPermissions()

        }
    }

    private fun checkLocationPermissions(): Boolean {
        return applicationContext.hasLocationPermissionGranted(applicationContext)
    }

    private fun startGpsMonitoring() {
        viewModelScope.launch {
            gpsStatus.observe()
                .collect { isGpsEnabled ->
                    updateUiState { copy(isGpsEnabled = isGpsEnabled) }
                    locationPermissionUseCase.invoke(true)
                    if (!isGpsEnabled) {
                        gpsControllerUseCase.invoke(false)
                        locationPermissionUseCase.invoke(true)
                        verifyGpsSettings()
                        emitUiEffect(LocationPermissionContract.UiEffect.UpdateActionButtonText)
                    } else {
                        gpsControllerUseCase.invoke(false)
                        locationPermissionUseCase.invoke(true)
                        navigateToNextScreen()
                    }
                }
        }
    }

    private fun verifyGpsSettings() {
        viewModelScope.launch {
            val locationRequest = createLocationRequest()
            val locationSettingsRequest = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true)
                .build()

            try {
                LocationServices.getSettingsClient(applicationContext)
                    .checkLocationSettings(locationSettingsRequest)
                    .addOnSuccessListener {
                        viewModelScope.launch {

                        }
                    }
                    .addOnFailureListener { exception ->
                        if (exception is ResolvableApiException) {
                            val intentSenderRequest = IntentSenderRequest
                                .Builder(exception.resolution)
                                .build()
                            viewModelScope.launch {
                                emitUiEffect(
                                    LocationPermissionContract.UiEffect.ShowGpsResolutionDialog(
                                        intentSenderRequest
                                    )
                                )
                            }
                        }
                    }
            } catch (exception: Exception) {
                handleGpsError(exception)
            }
        }
    }


    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_UPDATE_INTERVAL)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(MIN_UPDATE_INTERVAL)
            .setMaxUpdateDelayMillis(MAX_UPDATE_DELAY)
            .build()
    }


    private fun handleGpsError(exception: Exception) {
        updateUiState {
            copy(
                isGpsEnabled = false,
                errorMessage = exception.message
            )
        }
    }

    private fun navigateToAppSettings() {
        val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", applicationContext.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        applicationContext.startActivity(settingsIntent)
    }

    private fun requestLocationPermissions() {
        viewModelScope.launch {
            emitUiEffect(LocationPermissionContract.UiEffect.RequestLocationPermission)
        }
    }

    private fun navigateToNextScreen() {
        viewModelScope.launch {
            emitUiEffect(LocationPermissionContract.UiEffect.NavigateToNextScreen)
        }
    }

    private fun updateUiState(transform: LocationPermissionContract.UiState.() -> LocationPermissionContract.UiState) {
        _uiState.update(transform)
    }

    private suspend fun emitUiEffect(effect: LocationPermissionContract.UiEffect) {
        _uiEffect.send(effect)
    }

    companion object {
        private const val LOCATION_UPDATE_INTERVAL = 1000L
        private const val MIN_UPDATE_INTERVAL = 1000L
        private const val MAX_UPDATE_DELAY = 5000L
    }
}