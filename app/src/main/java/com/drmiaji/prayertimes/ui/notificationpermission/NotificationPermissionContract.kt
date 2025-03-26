package com.drmiaji.prayertimes.ui.notificationpermission

object NotificationPermissionContract {

    data class UiState(
        val isPermissionGranted: Boolean? = null,
        val requestStatus: RequestStatus = RequestStatus.INITIAL
    )

    enum class RequestStatus {
        INITIAL,
        REQUESTED,
        DENIED,
        PERMANENTLY_DENIED,
        GRANTED
    }

    sealed class UiAction {
        data object RequestPermission : UiAction()
        data object OpenAppSettings : UiAction()
        data object SkipPermission : UiAction()
    }

    sealed class UiEffect {
         object PermissionRequest : UiEffect()
         object NavigateToHome : UiEffect()
         object NavigateToSettings : UiEffect()
         object UpdateButtonState : UiEffect()
    }
}