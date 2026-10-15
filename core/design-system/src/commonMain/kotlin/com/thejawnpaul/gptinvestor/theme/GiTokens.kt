package com.thejawnpaul.gptinvestor.theme

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

object GiRadius {
    val xs = RoundedCornerShape(6.dp)
    val sm = RoundedCornerShape(8.dp)
    val md = RoundedCornerShape(12.dp)
    val lg = RoundedCornerShape(16.dp)
    val full = RoundedCornerShape(percent = 50)
}

object GiSpace {
    val x1 = 4.dp
    val x2 = 8.dp
    val x3 = 12.dp
    val x4 = 16.dp
    val x5 = 20.dp
    val x6 = 24.dp
    val x8 = 32.dp
    val x10 = 40.dp
    val gutter = 20.dp
    val topInset = 52.dp
    val cardPad = 16.dp
    val sectionGap = 24.dp
    val titleToContent = 12.dp
    val navHeight = 84.dp
    val settingsRow = 52.dp
    val stockRow = 64.dp
    val safeArea = 34.dp
    val touchTarget = 44.dp
}

object GiEdge {
    val width = 1.dp

    // Form text fields only, in colors.control so the boundary meets WCAG 3:1.
    // The Ask composer and search fields keep the line hairline on purpose.
    val fieldWidth = 1.dp
    val focusWidth = 2.dp
}

// Cards never get a shadow: only the floating Ask composer (lift) and overlays.
object GiElevation {
    fun lift(isDark: Boolean) = Shadow(
        radius = 16.dp,
        color = if (isDark) Color(0x3D000000) else Color(0x140F121B),
        offset = DpOffset(0.dp, 4.dp),
    )

    fun overlay(isDark: Boolean) = Shadow(
        radius = 32.dp,
        color = if (isDark) Color(0x59000000) else Color(0x1F0D1118),
        offset = DpOffset(0.dp, 7.dp),
    )
}

object GiMotion {
    const val INSTANT = 100
    const val FAST = 150
    const val BASE = 200
    const val SLOW = 300

    const val PRESSED_SCALE = 0.98f
    const val PRESSED_ALPHA = 0.12f
    const val DISABLED_ALPHA = 0.40f

    fun <T> snappy(): SpringSpec<T> = spring(dampingRatio = 0.85f, stiffness = 600f)

    fun <T> smooth(): SpringSpec<T> = spring(dampingRatio = 0.9f, stiffness = 300f)
}
