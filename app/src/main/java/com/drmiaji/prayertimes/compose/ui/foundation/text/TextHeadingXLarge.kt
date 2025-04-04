package com.drmiaji.prayertimes.compose.ui.foundation.text

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.drmiaji.prayertimes.compose.ui.theme.AlifThemes
import com.drmiaji.prayertimes.compose.ui.theme.Black
import com.drmiaji.prayertimes.compose.ui.theme.Primary
import com.drmiaji.prayertimes.compose.ui.theme.White

@Composable
fun TextHeadingXLarge(
    modifier: Modifier? = Modifier,
    text: String,
    textColor: Color? = null,
    textStyle: TextStyle? = null,
    textAlign: TextAlign? = null,
) {
    Text(
        modifier = modifier ?: Modifier,
        text = text,
        color = textColor ?: if (isSystemInDarkTheme()) White else Black,
        textAlign = textAlign,
        style = textStyle ?: AlifThemes.TextStyles.headingXLarge
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewTextHeadingXLarge() {
    MaterialTheme {
        TextHeadingXLarge(text = "Add new schedule", textColor = Primary)
    }
}