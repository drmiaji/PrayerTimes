package com.drmiaji.prayertimes.compose.page

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drmiaji.prayertimes.compose.ui.components.ItemCalendar
import com.drmiaji.prayertimes.compose.ui.components.ItemMainDate
import com.drmiaji.prayertimes.compose.ui.components.ItemProgressActivity
import com.drmiaji.prayertimes.compose.ui.components.ItemSchedule
import com.drmiaji.prayertimes.compose.ui.components.ItemTimingSchedule
import com.drmiaji.prayertimes.compose.ui.components.dummyCalendar
import com.drmiaji.prayertimes.compose.ui.components.dummyDescNextPray
import com.drmiaji.prayertimes.compose.ui.components.dummyListProgressTask
import com.drmiaji.prayertimes.compose.ui.components.dummyLocationAddress
import com.drmiaji.prayertimes.compose.ui.components.dummyNextPray
import com.drmiaji.prayertimes.compose.ui.components.dummyTimingSchedule
import com.drmiaji.prayertimes.data.model.DateSchedule
import com.drmiaji.prayertimes.data.model.Prayer
import com.drmiaji.prayertimes.data.model.ProgressTask
import com.drmiaji.prayertimes.data.model.TimingSchedule

@Composable
fun HomePage(
    calendar: DateSchedule,
    timingSchedule: TimingSchedule,
    progressListTask: List<ProgressTask>,
    locationAddress: String,
    timeNextPray: String,
    descNextPray: String,
    getInterval: (timingSchedule: TimingSchedule, nearestSchedule: Prayer) -> Unit,
    onSetReminder: (timingSchedule: TimingSchedule, prayerTime: String, isReminded: Boolean, position: Int) -> Unit,
    goToDetailCalendar: () -> Unit,
    goToProgressActivity: () -> Unit,
) {
    val context = LocalContext.current

    Scaffold(Modifier.padding(16.dp)) {
        LazyColumn(contentPadding = it) {
            item { ItemMainDate(calendar) }
            item {
                ItemTimingSchedule(
                    timingSchedule,
                    locationAddress,
                    timeNextPray,
                    descNextPray,
                    getInterval
                )
            }
          // item { ItemSchedule(timingSchedule.imsak, timingSchedule, onSetReminder) }
            item { ItemSchedule(timingSchedule.fajr, timingSchedule, onSetReminder) }
            item { ItemSchedule(timingSchedule.sunrise, timingSchedule, onSetReminder) }
            item { ItemSchedule(timingSchedule.dhuhr, timingSchedule, onSetReminder) }
            item { ItemSchedule(timingSchedule.asr, timingSchedule, onSetReminder) }
            item { ItemSchedule(timingSchedule.maghrib, timingSchedule, onSetReminder) }
            item { ItemSchedule(timingSchedule.isha, timingSchedule, onSetReminder) }

            item { ItemProgressActivity(progressListTask, goToProgressActivity) }
            item { ItemCalendar(calendar, goToDetailCalendar) }
        }
    }
}

@SuppressLint("MissingPermission")
@Preview
@Composable
private fun PreviewHomePage() {
    HomePage(
        dummyCalendar, dummyTimingSchedule, dummyListProgressTask, dummyLocationAddress,
        dummyNextPray, dummyDescNextPray, { _, _ -> }, { _, _, _, _ -> }, {}, {}
    )
}