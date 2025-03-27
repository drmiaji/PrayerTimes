package com.drmiaji.prayertimes.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.drmiaji.prayertimes.compose.page.SplashPage
import com.drmiaji.prayertimes.compose.ui.theme.AlifTheme
import com.drmiaji.prayertimes.ui.main.MainActivity
import com.drmiaji.prayertimes.utils.LocationUtils

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AlifTheme { SplashPage() } }
        requestLocationPermission()
    }

    @SuppressLint("NewApi")
    fun requestLocationPermission() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            LocationUtils.handlePermission(permissions)
            Handler(mainLooper).postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }, 1000L)
        }
        LocationUtils.launchPermission(locationPermissionRequest)
    }
}