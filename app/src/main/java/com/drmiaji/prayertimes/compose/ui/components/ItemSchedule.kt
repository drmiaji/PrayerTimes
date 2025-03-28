package com.drmiaji.prayertimes.compose.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drmiaji.prayertimes.R
import com.drmiaji.prayertimes.compose.ui.foundation.text.TextBody
import com.drmiaji.prayertimes.compose.ui.foundation.text.TextSubtitle
import com.drmiaji.prayertimes.compose.ui.theme.AlifThemes
import com.drmiaji.prayertimes.compose.ui.theme.Black10
import com.drmiaji.prayertimes.compose.ui.theme.Gray
import com.drmiaji.prayertimes.compose.ui.theme.Primary
import com.drmiaji.prayertimes.compose.ui.theme.Primary10
import com.drmiaji.prayertimes.data.model.Prayer
import com.drmiaji.prayertimes.data.model.TimingSchedule
import com.drmiaji.prayertimes.data.model.getNearestSchedule
import com.drmiaji.prayertimes.data.model.getScheduleName
import com.drmiaji.prayertimes.data.model.toList
import com.google.firebase.Timestamp

@Composable
fun ItemSchedule(
    prayer: Prayer,
    timingSchedule: TimingSchedule,
    onSetReminder: (timingSchedule: TimingSchedule, prayerTime: String, isReminded: Boolean, position: Int) -> Unit
) {
    val nearestSchedule = timingSchedule.getNearestSchedule(Timestamp.now())
    val isNowSchedule = nearestSchedule.time == prayer.time
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            if (isNowSchedule) 2.dp else 1.dp,
            if (isNowSchedule) Primary else {
                if (isSystemInDarkTheme()) Gray else Black10
            }
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isNowSchedule) Primary10 else AlifThemes.Colors.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isNowSchedule) 0.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp), // ⬅️ Reduce vertical space
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(4.dp))
            TextSubtitle(text = timingSchedule.getScheduleName(prayer))
            Spacer(modifier = Modifier.weight(5f))
            TextBody(text = prayer.time)
            Spacer(modifier = Modifier.width(4.dp))
            Image(
                modifier = Modifier
                    .padding(8.dp)
                    .width(25.dp)
                    .height(25.dp)
                    .aspectRatio(1f)
                    .clickable {
                        onSetReminder(
                            timingSchedule, prayer.time, !prayer.isReminded,
                            timingSchedule.toList().indexOf(prayer)
                        )
                    },
                painter = painterResource(
                    id = if (prayer.isReminded) R.drawable.ic_sound_on else R.drawable.ic_sound_off
                ),
                colorFilter = ColorFilter.tint(Primary),
                contentDescription = ""
            )
        }
    }
}

val dummyTimingSchedule = TimingSchedule(
  //  Prayer("04:40 (WIB)", false),
    Prayer("04:50 (WIB)", true),
    Prayer("05:51 (WIB)", false),
    Prayer("11:44 (WIB)", false),
    Prayer("15:06 (WIB)", true),
    Prayer("17:37 (WIB)", false),
    Prayer("18:38 (WIB)", false),
)

@Preview(showBackground = false)
@Composable
private fun PreviewItemSchedule() {
    ItemSchedule(
        dummyTimingSchedule.fajr,
        dummyTimingSchedule
    ) { _, _, _, _ -> }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewItemScheduleDark() {
    ItemSchedule(
        dummyTimingSchedule.fajr,
        dummyTimingSchedule
    ) { _, _, _, _ -> }
}

@Preview(showBackground = true)
@Composable
private fun PreviewItemScheduleSelected() {
    ItemSchedule(
        dummyTimingSchedule.asr,
        dummyTimingSchedule
    ) { _, _, _, _ -> }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewItemScheduleSelectedDark() {
    ItemSchedule(
        dummyTimingSchedule.asr,
        dummyTimingSchedule
    ) { _, _, _, _ -> }
}