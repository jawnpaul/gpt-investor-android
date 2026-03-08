package com.thejawnpaul.gptinvestor.theme

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
internal actual fun SetPlatformColors(
    statusBarColor: Color,
    navigationBarColor: Color,
    useDarkTheme: Boolean
) {
    val view = LocalView.current
    SideEffect {
        if (view.isInEditMode) return@SideEffect
        val window = (view.context as? Activity)?.window ?: return@SideEffect

        // Set transparent colors
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        // Configure appearance
        val windowInsetsController = WindowCompat.getInsetsController(window, view)
        windowInsetsController.isAppearanceLightStatusBars = !useDarkTheme
        windowInsetsController.isAppearanceLightNavigationBars = !useDarkTheme
    }
}