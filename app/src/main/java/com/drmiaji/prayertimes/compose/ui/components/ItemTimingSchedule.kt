package com.drmiaji.prayertimes.compose.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.firebase.Timestamp
import com.drmiaji.prayertimes.R
import com.drmiaji.prayertimes.compose.ui.foundation.text.*
import com.drmiaji.prayertimes.compose.ui.theme.Primary
import com.drmiaji.prayertimes.compose.ui.theme.White
import com.drmiaji.prayertimes.compose.ui.theme.White20
import com.drmiaji.prayertimes.data.model.Prayer
import com.drmiaji.prayertimes.data.model.TimingSchedule
import com.drmiaji.prayertimes.data.model.getNearestSchedule
import com.drmiaji.prayertimes.utils.TimeUtils.hour

@Composable
fun ItemTimingSchedule(
    timingSchedule: TimingSchedule,
    locationAddress: String,
    timeNextPray: String,
    descNextPray: String,
    getInterval: (timingSchedule: TimingSchedule, nearestSchedule: Prayer) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Primary // ✅ replaces backgroundColor
        )
    ) {
        val nearestSchedule = timingSchedule.getNearestSchedule(Timestamp.now())
        if (nearestSchedule.time != "-") {
            if (timeNextPray == "-") getInterval(timingSchedule, nearestSchedule)
        }
        Box {
            Image(
                painterResource(id = R.drawable.ic_bg_schedule),
                contentDescription = "",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.matchParentSize()
            )
            ConstraintLayout(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
            ) {
                val (iconReminder, textReminder, textTimeNextPray, textNextPray, textNearestScheduleName, textNearestScheduleTime, animation, textLocation) = createRefs()
                Box(
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(White20)
                        .constrainAs(iconReminder) {
                            top.linkTo(parent.top, margin = 8.dp)
                            start.linkTo(parent.start)
                            width = Dimension.ratio("1:1")
                            height = Dimension.value(32.dp)
                        }, contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier.padding(4.dp), painter = painterResource(
                            id = if (nearestSchedule.isReminded) R.drawable.ic_sound_on else R.drawable.ic_sound_off
                        ), contentDescription = "", colorFilter = ColorFilter.tint(White)
                    )
                }
                TextTitle(
                    modifier = Modifier.constrainAs(textReminder) {
                        top.linkTo(iconReminder.top)
                        bottom.linkTo(iconReminder.bottom)
                        start.linkTo(iconReminder.end, margin = 8.dp)
                    }, text = if (nearestSchedule.isReminded) "On" else "Off", textColor = White
                )
                TextSubtitle(
                    modifier = Modifier.constrainAs(textTimeNextPray) {
                        top.linkTo(parent.top, margin = 16.dp)
                        end.linkTo(textNearestScheduleName.start)
                    }, text = timeNextPray, textColor = White
                )
                TextBody(
                    modifier = Modifier.constrainAs(textNearestScheduleName) {
                        end.linkTo(parent.end)
                        top.linkTo(parent.top, margin = 16.dp)
                    }, text = descNextPray, textColor = White
                )
                val composition by rememberLottieComposition(
                    LottieCompositionSpec.RawRes(
                        if (Timestamp.now().hour in (19..23) || Timestamp.now().hour in (0..5)) R.raw.an_night else R.raw.an_day
                    )
                )
                LottieAnimation(composition,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier.constrainAs(animation) {
                        top.linkTo(textTimeNextPray.bottom)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.ratio("1:1")
                        height = Dimension.value(150.dp)
                    })

                TextSubtitle(
                    modifier = Modifier.constrainAs(textNextPray) {
                        bottom.linkTo(textNearestScheduleTime.top)
                        start.linkTo(parent.start)
                    }, text = "Next Prayer Time", textColor = White
                )

                TextHeadingXLarge(
                    modifier = Modifier.constrainAs(textNearestScheduleTime) {
                        top.linkTo(animation.top, margin = 64.dp)
                        bottom.linkTo(textLocation.top, margin = 16.dp)
                        start.linkTo(parent.start)
                    }, text = nearestSchedule.time, textColor = White
                )

                Row(modifier = Modifier.constrainAs(textLocation) {
                    bottom.linkTo(animation.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(animation.start)
                    width = Dimension.fillToConstraints
                }) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_location),
                        contentDescription = "",
                        modifier = Modifier.size(16.dp)
                    )
                    TextBody(
                        text = locationAddress, textColor = White
                    )
                }
            }
        }
    }
}

const val dummyLocationAddress = "Malmö, Sweden"
const val dummyNextPray = "1h 18m 3s "
const val dummyDescNextPray = "Asr"

@SuppressLint("MissingPermission")
@Preview
@Composable
private fun PreviewItemTimingSchedule() {
    LazyColumn {
        item {
            ItemTimingSchedule(
                dummyTimingSchedule,
                dummyLocationAddress,
                dummyNextPray,
                dummyDescNextPray
            ) { _, _ -> }
        }
    }
}