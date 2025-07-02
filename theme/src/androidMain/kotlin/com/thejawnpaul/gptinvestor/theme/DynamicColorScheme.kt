package com.thejawnpaul.gptinvestor.theme

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
internal actual fun rememberDynamicColorScheme(
    useDarkTheme: Boolean,
    dynamicColorEnabled: Boolean
): ColorScheme? {
    return if (dynamicColorEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)  {
        val context = LocalContext.current
        if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(
            context
        )
    } else null
}