package com.thejawnpaul.gptinvestor.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
internal expect fun SetPlatformColors(
    statusBarColor: Color,
    navigationBarColor: Color,
    useDarkTheme: Boolean
)