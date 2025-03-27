package com.drmiaji.prayertimes.ui.compass

import android.annotation.SuppressLint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.drmiaji.prayertimes.compose.page.CompassPage
import com.drmiaji.prayertimes.compose.ui.theme.AlifTheme
import com.drmiaji.prayertimes.data.model.RotationTarget
import com.drmiaji.prayertimes.utils.LocationUtils
import com.drmiaji.prayertimes.utils.LocationUtils.checkLocationPermission


class CompassActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLocation: Location
    private lateinit var sensorManager: SensorManager

    private var accelerometerValues: FloatArray? = null
    private var magneticValues: FloatArray? = null

    private var currentDegree = 0f
    private var currentDegreeNeedle = 0f

    private val model: CompassViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlifTheme {
                CompassPage(
                    model.isFacingQibla,
                    model.qilbaRotation,
                    model.compassRotation,
                    model.locationAddress,
                    goToBack = { finish() },
                    refreshLocation = {
                        if (this::sensorManager.isInitialized) {
                            sensorManager.unregisterListener(this)
                        }
                        getLocation()
                    }
                )
            }
        }
        getLocation()
    }

    private fun getLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermission { requestLocationPermission() }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                currentLocation = it
                model.getLocationAddress(this, currentLocation)
                sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
                    sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
                }

                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.let {
                    sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
                }
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> accelerometerValues = event.values
            Sensor.TYPE_MAGNETIC_FIELD -> magneticValues = event.values
        }

        if (accelerometerValues != null && magneticValues != null) {
            val rotationMatrix = FloatArray(9)
            val orientation = FloatArray(3)

            val success = SensorManager.getRotationMatrix(
                rotationMatrix, null,
                accelerometerValues, magneticValues
            )

            if (success) {
                SensorManager.getOrientation(rotationMatrix, orientation)

                val azimuthRadians = orientation[0]
                val azimuthDegrees = Math.toDegrees(azimuthRadians.toDouble()).toFloat()
                val degree = (azimuthDegrees + 360) % 360

                // Qibla logic
                val destinationLoc = Location("service Provider").apply {
                    latitude = 21.422487
                    longitude = 39.826206
                }

                var bearTo = currentLocation.bearingTo(destinationLoc)
                if (bearTo < 0) bearTo += 360

                var direction = bearTo - degree
                if (direction < 0) direction += 360

                val isFacingQibla = direction in 359.0..360.0 || direction in 0.0..1.0

                val qiblaRotation = RotationTarget(currentDegreeNeedle, direction)
                currentDegreeNeedle = direction
                val compassRotation = RotationTarget(currentDegree, -degree)
                currentDegree = -degree

                model.updateCompass(qiblaRotation, compassRotation, isFacingQibla)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        if (this::sensorManager.isInitialized) {
            sensorManager.unregisterListener(this)
        }
    }

    @SuppressLint("NewApi")
    fun requestLocationPermission() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            LocationUtils.handlePermission(permissions)
        }
        LocationUtils.launchPermission(locationPermissionRequest)
    }
}