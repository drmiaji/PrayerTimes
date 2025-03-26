package com.drmiaji.prayertimes.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.drmiaji.prayertimes.R

sealed class BottomNavItem(
    @DrawableRes val iconFill: Int,
    @DrawableRes val iconOutline: Int,
    val route: String,
) {
    data object Home : BottomNavItem(R.drawable.ic_prayer_fill, R.drawable.ic_prayer_outline,"home")
    data object Qible : BottomNavItem(R.drawable.ic_qible_fill, R.drawable.ic_qible_outline, "qible")
}

