package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalDensity

private const val LARGE_TEXT_FONT_SCALE = 1.5f

// The spec's 200% text rules (stacked banners, wider cards, hidden nav labels) switch on here.
@Composable
@ReadOnlyComposable
internal fun isLargeText(): Boolean = LocalDensity.current.fontScale >= LARGE_TEXT_FONT_SCALE
