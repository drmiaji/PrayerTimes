package com.drmiaji.prayertimes.compose.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.google.firebase.Timestamp
import com.drmiaji.prayertimes.compose.ui.components.BaseTopBar
import com.drmiaji.prayertimes.compose.ui.components.ItemActivity
import com.drmiaji.prayertimes.compose.ui.foundation.text.TextBody
import com.drmiaji.prayertimes.compose.ui.foundation.text.TextTitle
import com.drmiaji.prayertimes.compose.ui.theme.AlifTheme
import com.drmiaji.prayertimes.compose.ui.theme.Gray
import com.drmiaji.prayertimes.compose.ui.theme.Primary
import com.drmiaji.prayertimes.compose.ui.theme.Secondary
import com.drmiaji.prayertimes.data.model.ProgressTask
import com.drmiaji.prayertimes.utils.TimeUtils.fullDate

@Composable
fun ProgressPage(
    onBack: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BaseTopBar(title = "Activity", onBack = onBack)
        }
    ) {
        ConstraintLayout(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
        ) {
            val (headerRef, divRef, listRef) = createRefs()
            ProgressHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 24.dp)
                    .constrainAs(headerRef) {
                        top.linkTo(parent.top)
                    },
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = Gray)
                    .constrainAs(divRef) {
                        top.linkTo(headerRef.bottom)
                    }
            )
            LazyColumn(
                modifier = Modifier
                    .constrainAs(listRef) {
                        top.linkTo(divRef.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    },
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(10) {
                    ItemActivity(
                        progressTask = ProgressTask(
                            1239281309829,
                            "Dhuhr",
                            123123213,
                            "0",
                            false
                        )
                    )
                }
                item {
                    Spacer(modifier = Modifier.size(78.dp))
                }
            }
        }
    }
}

@Composable
private fun ProgressHeader(
    modifier: Modifier? = Modifier
) {
    ConstraintLayout(
        modifier = modifier ?: Modifier
    ) {
        val (titleRef, progressRef, percentageRef, allRef, barRef) = createRefs()
        TextTitle(
            modifier = Modifier.constrainAs(titleRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            },
            text = Timestamp.now().fullDate
        )
        TextBody(
            modifier = Modifier.constrainAs(progressRef) {
                top.linkTo(titleRef.bottom)
                start.linkTo(parent.start)
            },
            text = "Progress: "
        )
        TextBody(
            modifier = Modifier.constrainAs(percentageRef) {
                top.linkTo(progressRef.top)
                start.linkTo(progressRef.end)
                bottom.linkTo(progressRef.bottom)
            },
            text = "75%",
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Bold
            )
        )
        TextBody(
            modifier = Modifier.constrainAs(allRef) {
                top.linkTo(titleRef.top)
                end.linkTo(parent.end)
                bottom.linkTo(titleRef.bottom)
            },
            text = "All(10)",
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Bold
            ),
            textColor = Primary
        )
        LinearProgressIndicator(
            progress = { 0.5f },
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .constrainAs(barRef) {
                    top.linkTo(percentageRef.bottom, 16.dp)
                },
            color = Secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = StrokeCap.Round
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewProgressHeader() {
    AlifTheme {
        ProgressHeader(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewProgressPage() {
    AlifTheme { ProgressPage {} }
}