package com.drmiaji.prayertimes.ui.qible



object QibleContract {
    data class UiState(
        val rotation: Float? = null,
        val qiblaDirection: Float? = null,
        val latitude: Double? = null,
        val longitude: Double? = null,
        val error: String? = null
    )

    sealed class UiAction {

    }

    sealed class UiEffect {

    }
}