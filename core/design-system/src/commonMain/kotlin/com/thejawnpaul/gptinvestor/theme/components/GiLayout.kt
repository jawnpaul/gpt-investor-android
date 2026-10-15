package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalDensity

private const val LARGE_TEXT_FONT_SCALE = 1.5f

@Composable
@ReadOnlyComposable
internal fun isLargeText(): Boolean = LocalDensity.current.fontScale >= LARGE_TEXT_FONT_SCALE
