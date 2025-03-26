package com.drmiaji.prayertimes.common

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.common.api.ResolvableApiException
import kotlinx.coroutines.flow.Flow


@SuppressLint("ComposableNaming")
@Composable
fun <T> Flow<T>.collectWithLifecycle(
    collect: suspend (T) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(this, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            this@collectWithLifecycle.collect { effect ->
                collect(effect)
            }
        }
    }
}

fun isTiramisuOrHigher(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
}


infix fun Context.hasLocationPermissionGranted(context: Context): Boolean {
    val requiredPermissions = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )
    return requiredPermissions.all { permission ->
        ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

}


fun Context.hasNotificationPermissionGranted(): Boolean {
    return if (isTiramisuOrHigher()) {
        ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        NotificationManagerCompat.from(this).areNotificationsEnabled()
    }
}

fun Context.hasNotificationPermissionDenied(): Boolean {
    return if (isTiramisuOrHigher()) {
        ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_DENIED
    } else {
        !NotificationManagerCompat.from(this).areNotificationsEnabled()
    }
}


fun Context.showGpsResolutionDialog(exception: ResolvableApiException) {
    val activity = this as? Activity
    activity?.let {
        try {
            exception.startResolutionForResult(it, 1001)
        } catch (e: IntentSender.SendIntentException) {
            e.printStackTrace()
        }
    }
}

fun Context.shouldShowNotificationPermissionRationale(): Boolean {
    return (this as? Activity)?.let {
        ActivityCompat.shouldShowRequestPermissionRationale(
            it,
            if (isTiramisuOrHigher()) {
                android.Manifest.permission.POST_NOTIFICATIONS
            } else {
                ""
            }
        )
    } ?: false
}

fun Geocoder.getLocationApi32AndBelow(latitude: Double, longitude: Double): String {
    return try {
        val addresses = getFromLocation(latitude, longitude, 1)
        if (!addresses.isNullOrEmpty()) {
            addresses[0]?.let { address ->
                buildString {
                    // En önemli adres bileşenlerini ekleyelim
                    address.subAdminArea?.let { append(it) }
                    address.adminArea?.let {
                        if (isNotEmpty()) append(", ")
                        append(it)
                    }
                }
            } ?: "$latitude,$longitude"
        } else {
            "$latitude,$longitude" // Adres bulunamazsa koordinatları döndür
        }
    } catch (e: Exception) {
        Log.e("Geocoder", "Error getting address: ${e.message}")
        "$latitude,$longitude" // Hata durumunda koordinatları döndür
    }
}

fun Geocoder.apiKeyPrayer(latitude: Double, longitude: Double): String {
    return try {
        val addresses = getFromLocation(latitude, longitude, 1)
        if (!addresses.isNullOrEmpty()) {
            addresses[0]?.let { address ->
                buildString {
                    append(latitude)
                    append(",")
                    append(longitude)
                }
            } ?: "$latitude,$longitude"
        } else {
            "$latitude,$longitude"
        }
    } catch (e: Exception) {
        Log.e("Geocoder", "Error getting address: ${e.message}")
        "$latitude,$longitude"
    }
}
