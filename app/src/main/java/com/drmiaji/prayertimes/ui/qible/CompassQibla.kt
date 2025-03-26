package com.drmiaji.prayertimes.ui.qible

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

class CompassQibla @Inject constructor (private val sensorManager: SensorManager) : SensorEventListener {
    private val compassSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private var qiblaDirection: Float = 0f
    private var currentRotation: Float = 0f


    private var onRotationChanged: ((Float) -> Unit)? = null

    fun startListening() {
        compassSensor?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    fun setOnRotationChangedListener(listener: (Float) -> Unit) {
        onRotationChanged = listener
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            val rotationMatrix = FloatArray(9)
            val orientationAngles = FloatArray(3)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            SensorManager.getOrientation(rotationMatrix, orientationAngles)
            currentRotation = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
            onRotationChanged?.invoke(currentRotation)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    fun calculateQiblaDirection(latitude: Double, longitude: Double) {
        val kaabaLat = 21.422487
        val kaabaLong = 39.826206

        val l1 = Math.toRadians(latitude)
        val l2 = Math.toRadians(kaabaLat)
        val dl = Math.toRadians(kaabaLong - longitude)

        val y = sin(dl)
        val x = cos(l1) * tan(l2) - sin(l1) * cos(dl)
        var qibla = Math.toDegrees(atan2(y, x))

        if (qibla < 0) qibla += 360
        qiblaDirection = qibla.toFloat()
    }

    fun getCurrentRotation() = currentRotation
    fun getQiblaDirection() = qiblaDirection
}
