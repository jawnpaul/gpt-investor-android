package com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component

import androidx.compose.ui.graphics.Color
import com.thejawnpaul.gptinvestor.theme.components.GiStock

internal fun tickerStock(
    ticker: String,
    title: String,
    subtitle: String,
    percent: Double?,
    imageUrl: String? = null
): GiStock = GiStock(
    initials = if (ticker.length <= 3) ticker else ticker.take(2),
    // No brand colour from the API yet: GiLogoTile falls back to inkTile / onInkTile.
    brandColor = Color.Unspecified,
    title = title,
    subtitle = subtitle,
    percent = percent,
    imageUrl = imageUrl
)
