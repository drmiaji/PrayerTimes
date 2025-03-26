package com.drmiaji.prayertimes.ui.qible

import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.drmiaji.prayertimes.ui.home.HomeContract

class QibleScreenPreviewParameter : PreviewParameterProvider<QibleContract.UiState> {
    override val values: Sequence<QibleContract.UiState>
        get() = sequenceOf(
            QibleContract.UiState(
                qiblaDirection = 151.0F,
                rotation = 38F,
                latitude = 41.0082,
                longitude = 28.9784
            )
        )
}
