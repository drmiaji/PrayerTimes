package com.drmiaji.prayertimes.ui.compass

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drmiaji.prayertimes.data.model.RotationTarget
import kotlinx.coroutines.launch
import java.util.Locale


class CompassViewModel : ViewModel() {

    var isFacingQibla by mutableStateOf(false)
    var qilbaRotation by mutableStateOf(RotationTarget(0f, 0f))
    var compassRotation by mutableStateOf(RotationTarget(0f, 0f))

    var locationAddress: String = ""
        private set

    fun updateCompass(qilba: RotationTarget, compass: RotationTarget, isFacing: Boolean) {
        isFacingQibla = isFacing
        qilbaRotation = qilba
        compassRotation = compass
    }

    @SuppressLint("MissingPermission")
    fun getLocationAddress(context: Context, location: Location) {
        viewModelScope.launch {
            val geocoder = Geocoder(context, Locale.getDefault())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Use the new API for Android 13+ (API 33+)
                geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1,
                    object : Geocoder.GeocodeListener {
                        override fun onGeocode(addresses: List<Address>) {
                            if (addresses.isNotEmpty()) {
                                processAddress(addresses.first())
                            }
                        }
                    }
                )
            } else {
                // Use the old API for older versions
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(location.latitude, location.longitude, 1)?.let { addresses ->
                    if (addresses.isNotEmpty()) {
                        processAddress(addresses.first())
                    }
                }
            }
        }
    }

    private fun processAddress(address: Address) {
        locationAddress = buildString {
            append(address.locality ?: "").append(", ")
            append(address.subAdminArea ?: "")
        }
    }
}