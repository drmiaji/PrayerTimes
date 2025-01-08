package app.ynemreuslu.prayertimes.ui.home

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import app.ynemreuslu.prayertimes.R
import app.ynemreuslu.prayertimes.common.collectWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.flow.Flow


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@RequiresPermission(
    anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
)
@Composable
fun HomeScreen(
    uiState: HomeContract.UiState,
    onAction: (HomeContract.UiAction) -> Unit,
    uiEffect: Flow<HomeContract.UiEffect>,
    mapNextScreen: () -> Unit,
) {
    val locationPermissions = remember {
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    }

    val locationPermissionState = rememberMultiplePermissionsState(
        permissions = locationPermissions,
    )


    uiEffect.collectWithLifecycle { effect ->
        when (effect) {
            is HomeContract.UiEffect.ShowPrayerTimesForDate -> {
                // Handle showing prayer times for a specific date
            }

            HomeContract.UiEffect.RequestLocationPermission -> {
                locationPermissionState.launchMultiplePermissionRequest()
            }
        }
    }
    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }

    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 8.dp)
                .animateContentSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
        ) {
            HeaderSection(
                locationInfo = "${uiState.location}",
                gregorianCalendar = "${uiState.gregorianDate}",
                hijriCalendar = "${uiState.hijriDate}",
                mapNextScreen = mapNextScreen,
            )
            Spacer(modifier = Modifier.height(16.dp))
            PrayerTimeCard(uiState)
            Spacer(modifier = Modifier.height(16.dp))
            VerseText()
            Spacer(modifier = Modifier.height(16.dp))
            PrayerTimesSection(uiState)
        }
    }

}


@Composable
private fun HeaderSection(
    locationInfo: String,
    gregorianCalendar: String,
    hijriCalendar: String,
    mapNextScreen: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LocationSection(locationInfo, mapNextScreen)
        CalendarSection(gregorianCalendar, hijriCalendar)

    }
}

@Composable
private fun LocationSection(
    locationInfo: String, mapNextScreen: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { mapNextScreen() }) {
        Icon(
            painter = painterResource(R.drawable.ic_location),
            contentDescription = stringResource(R.string.location_icon_description),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(56.dp)
                .padding(end = 12.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = locationInfo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.update_location),
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}


@Composable
private fun CalendarSection(
    gregorianCalendar: String, hijriCalendar: String
) {
    Column(
        horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        Text(
            text = gregorianCalendar,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = hijriCalendar,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


@Composable
fun PrayerTimeCard(uiState: HomeContract.UiState) {
    OutlinedCard(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = getPrayerName(prayerName = uiState.prayerIndex),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                TimeDisplay(uiState)

            }
        }

    }
}


@Composable
fun getPrayerName(prayerName: Int?): String {
    return when (prayerName) {
        1 -> stringResource(R.string.fajr_prayer_countdown)
        2 -> stringResource(R.string.sunrise_prayer_countdown)
        3 -> stringResource(R.string.dhuhr_prayer_countdown)
        4 -> stringResource(R.string.asr_prayer_countdown)
        5 -> stringResource(R.string.maghrib_prayer_countdown)
        6 -> stringResource(R.string.imsak_countdown)
        else -> stringResource(R.string.imsak_countdown)
    }
}

@Composable
fun TimeDisplay(uiState: HomeContract.UiState) {
    Row(
        horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${uiState.prayerHours}",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "${uiState.prayerMinutes}",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "${uiState.prayerSeconds}",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

//
@Composable
private fun PrayerTimeItem(
    uiState: HomeContract.UiState,
    prayerIndex: Int,
    icon: Int,
    time: String,
    contentDescription: String = "",
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {


        val color = if (uiState.prayerIndex == prayerIndex) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.primaryContainer
        }

        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            tint = color
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = time,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = color,
            fontSize = 16.sp
        )

        HorizontalDivider(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    }
}

@Composable
private fun PrayerTimesSection(uiState: HomeContract.UiState) {
    if (uiState.prayerTimings != null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PrayerTimeItem(
                        uiState = uiState,
                        prayerIndex = 1,
                        icon = R.drawable.ic_fajr_time,
                        time = uiState.prayerTimings.data.timings.fajrTime,
                    )
                    PrayerTimeItem(
                        uiState = uiState,
                        prayerIndex = 3,
                        icon = R.drawable.ic_dhuhr_time,
                        time = uiState.prayerTimings.data.timings.dhuhrTime
                    )
                    PrayerTimeItem(
                        uiState = uiState,
                        prayerIndex = 5,
                        icon = R.drawable.ic_maghrib_time,
                        time = uiState.prayerTimings.data.timings.maghribTime
                    )
                }

                Spacer(modifier = Modifier.width(1.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PrayerTimeItem(
                        uiState = uiState,
                        prayerIndex = 2,
                        icon = R.drawable.ic_sunrise,
                        time = uiState.prayerTimings.data.timings.sunriseTime
                    )
                    PrayerTimeItem(
                        uiState = uiState,
                        prayerIndex = 4,
                        icon = R.drawable.ic_asr_time,
                        time = uiState.prayerTimings.data.timings.asrTime
                    )
                    PrayerTimeItem(
                        uiState = uiState,
                        prayerIndex = 6,
                        icon = R.drawable.ic_isha_time,
                        time = uiState.prayerTimings.data.timings.ishaTime
                    )
                }
            }

            Row(
                modifier = Modifier.matchParentSize(),
                horizontalArrangement = Arrangement.Center
            ) {
                VerticalDivider(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .width(1.dp)
                )
            }
        }
    }
}


@Composable
fun VerseText() {
    val verse = stringResource(id = R.string.verse_text)
    val surahName = stringResource(id = R.string.surah_name)
    val verseNumber = stringResource(id = R.string.verse_number)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = verse,
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            fontFamily = FontFamily.Cursive
        )
        Text(
            text = "$surahName, $verseNumber",
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.ExtraLight,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}