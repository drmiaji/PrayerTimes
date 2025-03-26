package com.drmiaji.prayertimes.ui.locationpermission


import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.common.api.ResolvableApiException

object LocationPermissionContract {
    data class UiState(
        val isPermissionGranted: Boolean? = null,
        val isGpsEnabled: Boolean? = null,
        val latitude: Double? = null,
        val longitude: Double? = null,
        val errorMessage: String? = null
    )

    sealed class UiAction {
        object RequestPermission : UiAction()
        object OpenAppSettings : UiAction()
        object OnPermissionGranted : UiAction()
    }

    sealed class UiEffect {
        object RequestLocationPermission : UiEffect()
        object NavigateToNextScreen : UiEffect()
        data class ShowGpsResolutionDialog(
            val request: IntentSenderRequest
        ) : UiEffect()

        object UpdateActionButtonText : UiEffect()
    }
}