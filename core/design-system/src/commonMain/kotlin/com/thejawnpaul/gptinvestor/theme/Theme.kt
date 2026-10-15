package com.thejawnpaul.gptinvestor.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

val LocalGPTInvestorColors = staticCompositionLocalOf { gptInvestorColorsDark }

@Composable
fun GPTInvestorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    userThemePreference: String? = null,
    content: @Composable () -> Unit,
) {

    val useDarkTheme = when (userThemePreference) {
        "Light" -> false
        "Dark" -> true
        "System" -> darkTheme
        else -> darkTheme
    }

    val giColors = if (useDarkTheme) giDarkColors else giLightColors
    val giType = rememberGiType()

    val gptInvestorColors = if (useDarkTheme) gptInvestorColorsDark else gptInvestorColorsLight

    SetPlatformColors(
        statusBarColor = Color.Transparent,
        navigationBarColor = Color.Transparent,
        useDarkTheme = useDarkTheme
    )

    val correctedDensity = Density(
        density = LocalDensity.current.density,
        fontScale = platformFontScale()
    )

    CompositionLocalProvider(
        LocalDensity provides correctedDensity,
        LocalGPTInvestorColors provides gptInvestorColors,
        LocalGiColors provides giColors,
        LocalGiType provides giType
    ) {
        MaterialTheme(
            colorScheme = giColors.toColorScheme(),
            typography = giType.toTypography(),
            content = content
        )
    }

}
