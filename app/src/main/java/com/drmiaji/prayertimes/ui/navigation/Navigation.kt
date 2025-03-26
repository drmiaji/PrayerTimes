package com.drmiaji.prayertimes.ui.navigation


import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.drmiaji.prayertimes.domain.usescase.GpsControllerUseCase
import com.drmiaji.prayertimes.domain.usescase.LocationPermissionUseCase
import com.drmiaji.prayertimes.ui.notificationpermission.NotificationPermissionScreen
import com.drmiaji.prayertimes.ui.locationpermission.LocationPermissionScreen
import com.drmiaji.prayertimes.ui.locationpermission.LocationPermissionViewModel
import com.drmiaji.prayertimes.ui.notificationpermission.NotificationPermissionViewModel
import com.drmiaji.prayertimes.domain.usescase.NotificationPermissionUseCase
import com.drmiaji.prayertimes.domain.usescase.SkipButtonUseCase
import com.drmiaji.prayertimes.ui.qible.QibleScreen
import com.drmiaji.prayertimes.ui.home.HomeScreen
import com.drmiaji.prayertimes.ui.home.HomeViewModel
import com.drmiaji.prayertimes.ui.qible.QibleViewModel



@Composable
fun SetupNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    locationPermissionUseCase: LocationPermissionUseCase,
    gspControllerUseCase: GpsControllerUseCase,
    notificationUseCase: NotificationPermissionUseCase,
    skipButtonUseCase: SkipButtonUseCase,
) {
    val context = LocalContext.current
    val startDestination = remember {
        when {
            locationPermissionUseCase.checkPermissionGranted() &&
                    gspControllerUseCase.isGpsDisabled() -> NavRoute.LOCATION_PERMISSION.route
            notificationUseCase.checkPermissionGranted() -> NavRoute.HOME.route
            skipButtonUseCase.isSkipButtonEnabled() -> NavRoute.HOME.route
            else -> NavRoute.LOCATION_PERMISSION.route
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                tween(300)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                tween(300)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                tween(300)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                tween(300)
            )
        },
        modifier = modifier
    ) {
        composable(route = NavRoute.LOCATION_PERMISSION.route) {
            val viewModel: LocationPermissionViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            LocationPermissionScreen(
                uiState = uiState,
                onAction = viewModel::onAction,
                uiEffect = viewModel.uiEffect,
                onNavigateToNextScreen = {
                    navController.navigate(NavRoute.NOTIFICATION_PERMISSION.route) {
                        popUpTo(NavRoute.LOCATION_PERMISSION.route) { inclusive = false }
                    }
                }
            )
        }

        composable(route = NavRoute.NOTIFICATION_PERMISSION.route) {
            val viewModel: NotificationPermissionViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            NotificationPermissionScreen(
                uiState = uiState,
                onAction = viewModel::onAction,
                uiEffect = viewModel.uiEffect,
                onNavigateToHomeScreen = {
                    navController.navigate(NavRoute.HOME.route) {
                        popUpTo(NavRoute.NOTIFICATION_PERMISSION.route) { inclusive = true }
                    }
                },
                onNavigateToOpenSettings = {
                    val settingsIntent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(settingsIntent)
                }
            )
        }

        composable(route = NavRoute.HOME.route) {
            val viewModel: HomeViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            HomeScreen(
                uiState = uiState,
                onAction = viewModel::onAction,
                uiEffect = viewModel.uiEffect,
            )
        }

        composable(route = NavRoute.QIBLE.route) {
            val viewModel: QibleViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            QibleScreen(
                uiState = uiState,
                onAction = viewModel::onAction,
                viewModel.uiEffect
            )
        }

    }
}

