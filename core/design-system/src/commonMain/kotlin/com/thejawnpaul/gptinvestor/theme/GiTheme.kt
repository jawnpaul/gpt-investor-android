package com.thejawnpaul.gptinvestor.theme

import androidx.compose.runtime.Composable

object GiTheme {
    val colors: GiColors
        @Composable get() = LocalGiColors.current

    val type: GiType
        @Composable get() = LocalGiType.current
}
