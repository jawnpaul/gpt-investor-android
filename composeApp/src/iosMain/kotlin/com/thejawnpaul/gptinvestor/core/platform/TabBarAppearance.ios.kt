package com.thejawnpaul.gptinvestor.core.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import platform.UIKit.UIColor
import platform.UIKit.UIDevice
import platform.UIKit.UITabBar
import platform.UIKit.UITabBarAppearance

private fun Color.toUIColor() = UIColor(
    red = red.toDouble(),
    green = green.toDouble(),
    blue = blue.toDouble(),
    alpha = alpha.toDouble()
)

@Composable
actual fun ApplyTabBarAppearance(background: Color, line: Color) {
    remember(background, line) {
        val isLiquidGlass = UIDevice.currentDevice.systemVersion.substringBefore('.').toIntOrNull()?.let { it >= 26 }
        if (isLiquidGlass != false) return@remember
        val appearance = UITabBarAppearance().apply {
            configureWithOpaqueBackground()
            backgroundColor = background.toUIColor()
            shadowColor = line.toUIColor()
        }
        UITabBar.appearance().standardAppearance = appearance
        UITabBar.appearance().scrollEdgeAppearance = appearance
    }
}
