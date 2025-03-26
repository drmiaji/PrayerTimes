package com.drmiaji.prayertimes.ui.notificationpermission


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.flow.Flow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.drmiaji.prayertimes.R
import com.drmiaji.prayertimes.common.collectWithLifecycle
import com.drmiaji.prayertimes.ui.notificationpermission.NotificationPermissionContract.RequestStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.flow.flow


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationPermissionScreen(
    uiState: NotificationPermissionContract.UiState,
    onAction: (NotificationPermissionContract.UiAction) -> Unit,
    uiEffect: Flow<NotificationPermissionContract.UiEffect>,
    onNavigateToHomeScreen: () -> Unit,
    onNavigateToOpenSettings: () -> Unit
) {

    val notificationPermission = rememberPermissionState(
        permission = android.Manifest.permission.POST_NOTIFICATIONS,
        onPermissionResult = { isGranted ->
            if (isGranted) {
                onNavigateToHomeScreen.invoke()
            }
        }
    )

    LaunchedEffect(notificationPermission.status.isGranted) {
        if (notificationPermission.status.isGranted) {
            onAction(NotificationPermissionContract.UiAction.SkipPermission)
        }
    }

    val notificationPermissionText = stringResource(R.string.grant_notification_permission)
    val goToSettingsText = stringResource(R.string.open_notification_settings)
    var permissionButtonText by remember { mutableStateOf(notificationPermissionText) }

    uiEffect.collectWithLifecycle { effect ->
        when (effect) {
            NotificationPermissionContract.UiEffect.PermissionRequest -> notificationPermission.launchPermissionRequest()
            NotificationPermissionContract.UiEffect.NavigateToHome -> onNavigateToHomeScreen.invoke()
            NotificationPermissionContract.UiEffect.NavigateToSettings -> onNavigateToOpenSettings.invoke()
            NotificationPermissionContract.UiEffect.UpdateButtonState -> permissionButtonText =
                goToSettingsText
        }
    }

    NotificationPermissionContent(
        uiState = uiState,
        onSkipClick = { onAction(NotificationPermissionContract.UiAction.SkipPermission) },
        onPermissionClick = {
            handlePermissionClick(
                requestStatus = uiState.requestStatus,
                onAction = onAction,
                homeNextScreen = onNavigateToHomeScreen,
            )
        },
        permissionButtonText = permissionButtonText
    )
}

@Composable
private fun NotificationPermissionContent(
    uiState: NotificationPermissionContract.UiState,
    onSkipClick: () -> Unit,
    onPermissionClick: () -> Unit,
    permissionButtonText: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NotificationInfoSection()
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ButtonSection(
                onSkipClick = onSkipClick,
                onPermissionClick = onPermissionClick,
                permissionButtonText = permissionButtonText,
            )
        }
    }
}

@Composable
private fun NotificationInfoSection() {
    NotificationAnimation(
        modifier = Modifier
            .fillMaxWidth()
            .size(350.dp)
    )
    PermissionText()
}

@Composable
private fun ButtonSection(
    onSkipClick: () -> Unit,
    onPermissionClick: () -> Unit,
    permissionButtonText: String,
) {
    SkipButton(onSkipClick = onSkipClick)
    PermissionRequestButton(onPermissionClick = onPermissionClick, permissionButtonText)
}

@Composable
private fun NotificationAnimation(modifier: Modifier = Modifier) {
    val animationComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.notification_animation)
    )

    val animationProgress by animateLottieCompositionAsState(
        composition = animationComposition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )

    LottieAnimation(
        composition = animationComposition,
        progress = { animationProgress }, // ✅ Lambda — fixed!
        modifier = modifier
    )
}

@Composable
private fun PermissionText() {
    Text(
        text = stringResource(id = R.string.notification_permission_prompt),
        style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        ),
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(all = 16.dp)
    )
}

@Composable
private fun PermissionRequestButton(onPermissionClick: () -> Unit, permissionButtonText: String) {
    Button(
        onClick = onPermissionClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colorScheme.onPrimary,
            containerColor = MaterialTheme.colorScheme.primary
        ),
        shape = MaterialTheme.shapes.medium,
    ) {
        Text(
            text = permissionButtonText,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun SkipButton(onSkipClick: () -> Unit) {
    TextButton(
        onClick = onSkipClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = stringResource(id = R.string.skip_button),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

private fun handlePermissionClick(
    requestStatus: RequestStatus,
    onAction: (NotificationPermissionContract.UiAction) -> Unit,
    homeNextScreen: () -> Unit
) {
    when (requestStatus) {
        RequestStatus.INITIAL,
        RequestStatus.REQUESTED,
        RequestStatus.DENIED -> {
            onAction(NotificationPermissionContract.UiAction.RequestPermission)
        }

        RequestStatus.GRANTED -> {
            homeNextScreen.invoke()
        }

        RequestStatus.PERMANENTLY_DENIED -> {
            onAction(NotificationPermissionContract.UiAction.OpenAppSettings)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationPermissionPreview(
    @PreviewParameter(NotificationPermissionPreviewParameter::class) uiState : NotificationPermissionContract.UiState
) {
    NotificationPermissionScreen(
        uiState =  uiState,
        onAction = {},
        uiEffect = flow {  },
        onNavigateToHomeScreen = {},
        onNavigateToOpenSettings = {}
    )
}