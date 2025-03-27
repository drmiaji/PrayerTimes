package com.drmiaji.prayertimes.compose.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drmiaji.prayertimes.R
import com.drmiaji.prayertimes.compose.ui.theme.*

@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    icon: Int,
    type: Int = 0,
    action: () -> Unit
) {
    Button(
        onClick = action,
        modifier = modifier
            .size(48.dp), // or 64.dp if you want larger tap area
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent, // ✅ No background
            contentColor = if (isSystemInDarkTheme() || type == 1) White else Black
        ),
        elevation = null, // ✅ No shadow
        contentPadding = PaddingValues(0.dp)
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(36.dp), // adjust as needed
//            colorFilter = ColorFilter.tint(
//                if (isSystemInDarkTheme() || type == 1) White else Black
//            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewActionButtonDark() {
    AlifTheme {
        ActionButton(icon = R.drawable.ic_repeat, type = 1) {}
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewActionButton() {
    AlifTheme {
        ActionButton(icon = R.drawable.ic_repeat) {}
    }
}