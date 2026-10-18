package com.thejawnpaul.gptinvestor.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
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

    val sheet = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
}

object GiSpace {
    val x0_5 = 2.dp
    val x1 = 4.dp
    val x1_5 = 6.dp
    val x2 = 8.dp
    val x2_5 = 10.dp
    val x3 = 12.dp
    val x4 = 16.dp
    val x5 = 20.dp
    val x6 = 24.dp
    val x7 = 28.dp
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

    val emphasis = 1.5.dp

    // Checkbox and radio borders.
    val control = 2.dp
}

object GiIconSize {
    val xs = 14.dp
    val sm = 16.dp
    val md = 20.dp
    val lg = 24.dp
    val xl = 40.dp
}

object GiControlHeight {
    // Delta, sentiment and PRO pills.
    val pill = 24.dp
    val chipS = 32.dp
    val buttonS = 36.dp

    // Minimum touch target, button M, icon button.
    val target = 44.dp

    // Search field, toast, nav item.
    val search = 48.dp

    // Text field, Ask composer.
    val field = 52.dp
    val buttonL = 56.dp
}

object GiSize {
    val tooltipMax = 280.dp
    val dialogMax = 320.dp
}

// Cards never get a shadow: only the floating Ask composer (lift) and overlays.
object GiElevation {
    fun lift(isDark: Boolean) = Shadow(
        radius = 16.dp,
        color = if (isDark) Color(0x3D000000) else Color(0x140F121B),
        offset = DpOffset(0.dp, 4.dp)
    )

    fun overlay(isDark: Boolean) = Shadow(
        radius = 32.dp,
        color = if (isDark) Color(0x59000000) else Color(0x1F0D1118),
        offset = DpOffset(0.dp, 7.dp)
    )
}

object GiMotion {
    const val INSTANT = 100
    const val FAST = 150
    const val BASE = 200
    const val SLOW = 300
    const val REVEAL = 1000

    const val PRESSED_SCALE = 0.98f
    const val PRESSED_ALPHA = 0.12f
    const val DISABLED_ALPHA = 0.40f

    // Dialogs scale 0.96 -> 1 on enter.
    const val ENTER_SCALE = 0.96f

    fun <T> snappy(): SpringSpec<T> = spring(dampingRatio = 0.85f, stiffness = 600f)

    fun <T> smooth(): SpringSpec<T> = spring(dampingRatio = 0.9f, stiffness = 300f)
}

object GiEasing {
    // Equivalent to EaseOutQuart: buttons, chips, fields, beam, step transitions.
    val emphasised: Easing = CubicBezierEasing(0.25f, 1f, 0.5f, 1f)
    val standard: Easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
    val linear: Easing = LinearEasing
}
