package com.drmiaji.prayertimes.ui.locationpermission

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class LocationPermissionPreviewParameter :
    PreviewParameterProvider<LocationPermissionContract.UiState> {

    override val values: Sequence<LocationPermissionContract.UiState>
        get() = sequenceOf(
            LocationPermissionContract.UiState(
                isPermissionGranted = false,
                isGpsEnabled = true
            )
        )
}
