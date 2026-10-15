package com.thejawnpaul.gptinvestor.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity

@Composable
internal actual fun platformFontScale(): Float = LocalDensity.current.fontScale * 0.88f