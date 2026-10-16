package com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component

import androidx.compose.ui.graphics.Color
import com.thejawnpaul.gptinvestor.theme.components.GiStock

// Logo tile fallback until the API sends brand colours: a stable colour per ticker.
private val TICKER_COLORS = listOf(
    Color(0xFF4CAF50),
    Color(0xFF2196F3),
    Color(0xFF9C27B0),
    Color(0xFFFF9800),
    Color(0xFFE91E63),
    Color(0xFF00BCD4),
    Color(0xFF607D8B)
)

internal fun tickerStock(
    ticker: String,
    title: String,
    subtitle: String,
    percent: Double?,
    imageUrl: String? = null
): GiStock = GiStock(
    initials = if (ticker.length <= 3) ticker else ticker.take(2),
    brandColor = TICKER_COLORS[(ticker.firstOrNull()?.code ?: 0) % TICKER_COLORS.size],
    title = title,
    subtitle = subtitle,
    percent = percent,
    imageUrl = imageUrl
)
