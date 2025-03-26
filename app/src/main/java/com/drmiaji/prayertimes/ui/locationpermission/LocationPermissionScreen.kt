package com.drmiaji.prayertimes.ui.locationpermission



import android.app.Activity.RESULT_OK
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.drmiaji.prayertimes.common.collectWithLifecycle
import kotlinx.coroutines.flow.Flow
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.drmiaji.prayertimes.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import kotlinx.coroutines.flow.flow


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionScreen(
    modifier: Modifier = Modifier,
    uiState: LocationPermissionContract.UiState,
    onAction: (LocationPermissionContract.UiAction) -> Unit,
    uiEffect: Flow<LocationPermissionContract.UiEffect>,
    onNavigateToNextScreen: () -> Unit,
) {

    val locationPermissionText = stringResource(R.string.grant_location_permission)
    val enableGpsText = stringResource(R.string.enable_gps)
    var currentButtonText by remember { mutableStateOf(locationPermissionText) }


    val locationPermissions = remember {
        listOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
    val settingResultRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { activityResult ->
        if (activityResult.resultCode == RESULT_OK)
            Log.d("LocationPermissionScreen", "Accepted")
        else {
            Log.d("LocationPermissionScreen", "Denied")
        }
    }


    val locationPermissionState = rememberMultiplePermissionsState(
        permissions = locationPermissions,
        onPermissionsResult = {
            handlePermissionResult(
                permissions = it,
                onAction = onAction
            )
        }
    )


    uiEffect.collectWithLifecycle { effect ->
        when (effect) {
            is LocationPermissionContract.UiEffect.RequestLocationPermission -> {
                locationPermissionState.launchMultiplePermissionRequest()
            }

            is LocationPermissionContract.UiEffect.NavigateToNextScreen -> {
                onNavigateToNextScreen.invoke()
            }

            is LocationPermissionContract.UiEffect.ShowGpsResolutionDialog -> {
                settingResultRequest.launch(effect.request)

            }

            is LocationPermissionContract.UiEffect.UpdateActionButtonText -> {
                currentButtonText = enableGpsText
            }
        }
    }

    LocationPermissionContent(
        onAction = onAction,
        buttonText = currentButtonText,
        modifier = modifier
    )
}

private fun handlePermissionResult(
    permissions: Map<String, Boolean>,
    onAction: (LocationPermissionContract.UiAction) -> Unit
) {
    if (permissions.all { it.value }) {
        onAction(LocationPermissionContract.UiAction.OnPermissionGranted)
    }
}


@Composable
private fun LocationPermissionContent(
    onAction: (LocationPermissionContract.UiAction) -> Unit,
    buttonText: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LocationHeader()
            LocationDescription()
        }

        LocationActionButton(
            onAction = onAction,
            buttonText = buttonText,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        )
    }
}

@Composable
private fun LocationHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LocationIcon()
        LocationTitle()
    }
}

@Composable
private fun LocationIcon(
    modifier: Modifier = Modifier
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_near),
        contentDescription = stringResource(R.string.location_icon_description),
        tint = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .size(72.dp)
            .padding(8.dp)
    )
}

@Composable
private fun LocationTitle(
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.location_title),
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Composable
private fun LocationDescription(
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.location_permission_message),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = modifier.padding(16.dp)
    )
}

@Composable
private fun LocationActionButton(
    onAction: (LocationPermissionContract.UiAction) -> Unit,
    buttonText: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = {
            onAction(
                LocationPermissionContract.UiAction.RequestPermission
            )
        },
        modifier = modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = MaterialTheme.shapes.medium,
    ) {
        Text(
            text = buttonText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LocationPermissionPreview(
    @PreviewParameter(LocationPermissionPreviewParameter::class) uiState : LocationPermissionContract.UiState
) {
    LocationPermissionScreen(
        uiState =  uiState,
        onAction = {},
        uiEffect = flow {  },
        onNavigateToNextScreen = {}
    )
}