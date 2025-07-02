package com.thejawnpaul.gptinvestor.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable

@Composable
internal expect fun PlatformColors(
    statusBarColor: Color,
    navigationBarColor: Color,
    useDarkTheme: Boolean
)