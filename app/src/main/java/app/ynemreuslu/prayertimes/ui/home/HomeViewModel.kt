package app.ynemreuslu.prayertimes.ui.home


import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.ynemreuslu.prayertimes.common.getLocationApi32AndBelow
import app.ynemreuslu.prayertimes.common.hasLocationPermissionGranted
import app.ynemreuslu.prayertimes.domain.prayer.PrayerTimings
import app.ynemreuslu.prayertimes.domain.usescase.GetPrayerTimingsUseCase
import app.ynemreuslu.prayertimes.domain.usescase.LocationPermissionUseCase
import app.ynemreuslu.prayertimes.domain.usescase.LocationUseCase
import app.ynemreuslu.prayertimes.util.Resource
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.isActive
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.time.chrono.HijrahDate
import java.time.temporal.TemporalAccessor


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPrayerTimingsUseCase: GetPrayerTimingsUseCase,
    @ApplicationContext private val appContext: Context,
    private val fusedLocationClient: FusedLocationProviderClient,
    private val locationUseCase: LocationUseCase,
    private val locationPermissionUseCase: LocationPermissionUseCase,
    private val geocoder: Geocoder,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeContract.UiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect by lazy { Channel<HomeContract.UiEffect>() }
    val uiEffect: Flow<HomeContract.UiEffect> by lazy { _uiEffect.receiveAsFlow() }
    private var countdownJob: Job? = null

    init {
        initializeViewModel()
    }

    fun onAction(action: HomeContract.UiAction) {
        when (action) {
            else -> {}
        }
    }

    private fun initializeViewModel() {
        loadLastKnownLocation()
        fetchLocation()
        fetchCalender()
    }

    private fun locationRequest() {
        viewModelScope.launch {
            if (locationPermissionUseCase.checkPermissionGranted()) {
                emitUiEffect(HomeContract.UiEffect.RequestLocationPermission)
            }
        }
    }

    private fun loadLastKnownLocation() {
        viewModelScope.launch {
            updateUiState {
                copy(
                    isLoading = true
                )
            }
            val location = buildLocationString(
                locationUseCase.getLatitude(),
                locationUseCase.getLongitude()
            )

            if (location != DEFAULT_LOCATION) {
                updateUiState { copy(location = location) }
                fetchPrayerTimings(location)
                updateUiState {
                    copy(isLoading = false)
                }
            }
        }
    }

    private fun fetchLocation() {
        viewModelScope.launch {
            if (checkLocationPermissions()) {
                initializeLocationUpdates()
            } else {
                loadLastKnownLocation()
            }
        }
    }

    private fun checkLocationPermissions(): Boolean =
        appContext.hasLocationPermissionGranted(appContext)

    private fun initializeLocationUpdates() {
        viewModelScope.launch {
            try {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        location?.let {
                            updateLocationData(it.latitude, it.longitude)
                        } ?: requestCurrentLocationUpdate()
                    }
                    .addOnFailureListener { exception ->
                        handleLocationFailure(exception)
                    }
            } catch (securityException: SecurityException) {
                handleSecurityException(securityException)
            }
        }
    }

    private fun updateLocationData(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            locationUseCase.apply {
                fetchLatitude(latitude)
                fetchLongitude(longitude)
            }
            updateUiState {
                copy(location = buildLocationString(latitude, longitude))
            }
            fetchPrayerTimings(buildLocationString(latitude, longitude))
        }

    }

    private fun fetchPrayerTimings(address: String) {
        viewModelScope.launch {
            getPrayerTimingsUseCase.invoke(
                date = formatDate(LocalDate.now(), Locale.getDefault()),
                address = address,
                method = METHOD
            )
                .onStart {
                    updateUiState { copy(isLoading = true) }
                }
                .onCompletion {
                    updateUiState { copy(isLoading = false) }
                }
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            updateUiState {
                                copy(prayerTimings = result.data)
                            }
                            startPrayerTimeCountdown(result.data)
                            locationRequest()
                        }

                        is Resource.Error -> {
                            updateUiState {
                                copy(errorPrayerTimings = result.message)
                            }
                        }
                    }
                }
        }
    }


    private fun requestCurrentLocationUpdate() {
        if (!checkLocationPermissions()) {
            loadLastKnownLocation()
        }
        try {
            val cancellationToken = CancellationTokenSource().token
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationToken
            ).apply {
                addOnSuccessListener(::handleLocationSuccess)
                addOnFailureListener(::handleLocationFailure)
            }
        } catch (securityException: SecurityException) {
            handleSecurityException(securityException)
        } catch (exception: Exception) {
            handleGeneralException(exception)
        }
    }

    private fun handleLocationSuccess(location: Location?) {
        location?.let {
            updateLocationData(it.latitude, it.longitude)
        } ?: run {
            handleLocationError("Unable to fetch current location")
            loadLastKnownLocation()
        }
    }

    private fun handleLocationFailure(exception: Exception) {
        logLocationError(exception)
        handleLocationError("Location update failed: ${exception.message}")
        loadLastKnownLocation()
    }

    private fun handleSecurityException(exception: SecurityException) {
        handleLocationError("Location permission denied: ${exception.message}")
        loadLastKnownLocation()
    }

    private fun handleGeneralException(exception: Exception) {
        logLocationError(exception)
        handleLocationError("Location update error: ${exception.message}")
        loadLastKnownLocation()
    }

    private fun handleLocationError(errorMessage: String) {
        Log.e(TAG, errorMessage)
        updateUiState { copy(location = errorMessage) }
    }

    private fun logLocationError(exception: Exception) {
        Log.e(TAG, "${exception.message}")
    }

    private fun formatDate(
        date: TemporalAccessor,
        locale: Locale = Locale.getDefault()
    ): String = DateTimeFormatter.ofPattern(DATE_PATTERN, locale).format(date)

    private fun getCurrentFormattedDate(): String =
        formatDate(LocalDate.now())

    private fun getHijriFormattedDate(): String =
        formatDate(HijrahDate.now())

    private fun fetchCalender() {
        updateUiState {
            copy(
                gregorianDate = getCurrentFormattedDate(),
                hijriDate = getHijriFormattedDate()
            )
        }
    }

    private fun startPrayerTimeCountdown(prayerTimings: PrayerTimings) {
        countdownJob?.cancel()

        countdownJob = viewModelScope.launch {
            while (isActive) {
                try {
                    findNextPrayer(prayerTimings)
                    delay(1000)
                } catch (e: Exception) {
                    Log.e(TAG, "Countdown error: ${e.message}")
                    break
                }
            }
        }
    }

    private fun findNextPrayer(prayerTimings: PrayerTimings) {
        val currentTime = LocalTime.now()
        val prayers = buildList {
            prayerTimings.data.timings.run {
                if (fajrTime.isNotEmpty()) add(Triple(1, "Fajr", convertToLocalTime(fajrTime)))
                if (sunriseTime.isNotEmpty()) add(
                    Triple(
                        2,
                        "Sunrise",
                        convertToLocalTime(sunriseTime)
                    )
                )
                if (dhuhrTime.isNotEmpty()) add(Triple(3, "Dhuhr", convertToLocalTime(dhuhrTime)))
                if (asrTime.isNotEmpty()) add(Triple(4, "Asr", convertToLocalTime(asrTime)))
                if (maghribTime.isNotEmpty()) add(
                    Triple(
                        5,
                        "Maghrib",
                        convertToLocalTime(maghribTime)
                    )
                )
                if (ishaTime.isNotEmpty()) add(Triple(6, "Isha", convertToLocalTime(ishaTime)))
            }
        }.sortedBy { it.third }

        val nextPrayer = prayers.firstOrNull { it.third.isAfter(currentTime) }
            ?: prayers.first()

        val duration =
            if (nextPrayer == prayers.first() && currentTime.isAfter(prayers.last().third)) {
                val nextDayFirstPrayer = nextPrayer.third
                val durationUntilMidnight = Duration.between(currentTime, LocalTime.MAX)
                val durationFromMidnight = Duration.between(LocalTime.MIN, nextDayFirstPrayer)
                durationUntilMidnight.plus(durationFromMidnight)
            } else {
                Duration.between(currentTime, nextPrayer.third)
            }

        updateUiState {
            copy(
                prayerIndex = nextPrayer.first,
                prayerHours = "%02d:".format(duration.toHours()),
                prayerMinutes = "%02d:".format(duration.toMinutes() % 60),
                prayerSeconds = "%02d".format(duration.seconds % 60)
            )
        }

        updateUiState {
            copy(
                prayerIndex = nextPrayer.first,
                prayerHours = "%02d:".format(duration.toHours()),
                prayerMinutes = "%02d:".format(duration.toMinutes() % 60),
                prayerSeconds = "%02d".format(duration.seconds % 60)
            )
        }
    }

    private fun convertToLocalTime(timeStr: String): LocalTime {
        return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"))
    }

    private fun buildLocationString(latitude: Double?, longitude: Double?): String {

        val lat = latitude ?: 0.0
        val lng = longitude ?: 0.0

        return if (lat == 0.0 && lng == 0.0) {
            DEFAULT_LOCATION
        } else {
            try {
                geocoder.getLocationApi32AndBelow(lat, lng)
            } catch (e: Exception) {
                Log.e(TAG, "Error building location string: ${e.message}")
                "$lat,$lng"
            }
        }
    }


    private suspend fun emitUiEffect(effect: HomeContract.UiEffect) {
        _uiEffect.send(effect)
    }

    private fun updateUiState(update: HomeContract.UiState.() -> HomeContract.UiState) {
        _uiState.update(update)
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }

    companion object {
        private const val TAG = "HomeViewModel"
        private const val DATE_PATTERN = "dd MMMM yyyy"
        private const val DEFAULT_LOCATION = "0.0,0.0"
        private const val METHOD = 13
    }
}