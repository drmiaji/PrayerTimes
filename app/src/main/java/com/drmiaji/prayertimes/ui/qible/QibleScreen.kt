package com.drmiaji.prayertimes.ui.qible


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drmiaji.prayertimes.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.roundToInt

@Composable
fun QibleScreen(
    uiState: QibleContract.UiState,
    onAction: (QibleContract.UiAction) -> Unit,
    uiEffect: Flow<QibleContract.UiEffect>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (uiState.qiblaDirection?.roundToInt() == uiState.rotation?.roundToInt()) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onPrimary
                }
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "${uiState.rotation?.roundToInt()}°",
            fontSize = 36.sp,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )

        val qiblaDirectionDegrees = uiState.qiblaDirection ?: 0f
        val rotationDegrees = uiState.rotation ?: 0f

        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {

            Image(
                painter = painterResource(id = R.drawable.ic_compass_direction),
                contentDescription = "Compass",
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(rotationDegrees)
            )

            Image(
                painter = painterResource(id = R.drawable.ic_qibla_needle),
                contentDescription = "Needle",
                modifier = Modifier
                    .size(200.dp)
                    .height(200.dp)
                    .width(200.dp)
                    .rotate(qiblaDirectionDegrees - rotationDegrees)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QibleScreenPreview(@PreviewParameter(QibleScreenPreviewParameter::class) uiState: QibleContract.UiState) {
    QibleScreen(
        uiState = uiState,
        onAction = {},
        uiEffect = flow { }
    )
}




