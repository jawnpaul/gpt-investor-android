package com.thejawnpaul.gptinvestor.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

val md_theme_light_primary = Color(0xFF433D48)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFD4E3FF)
val md_theme_light_onPrimaryContainer = Color(0xFF001C3A)
val md_theme_light_secondary = Color(0xFFB90C55)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFFFD9DF)
val md_theme_light_onSecondaryContainer = Color(0xFF3F0018)
val md_theme_light_tertiary = Color(0xFF875200)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFFFDDBA)
val md_theme_light_onTertiaryContainer = Color(0xFF2B1700)
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_onErrorContainer = Color(0xFF410002)
val md_theme_light_background = Color(0xFFFFFFFF)
val md_theme_light_onBackground = Color(0xFF433D48)
val md_theme_light_surface = Color(0xFFFAFAFA)
val md_theme_light_onSurface = Color(0xFF433D48)
val md_theme_light_surfaceVariant = Color(0xFF433D48)
val md_theme_light_onSurfaceVariant = Color(0xFF43474E)
val md_theme_light_outline = Color(0xFF74777F)
val md_theme_light_inverseOnSurface = Color(0xFFD6F6FF)
val md_theme_light_inverseSurface = Color(0xFF00363F)
val md_theme_light_inversePrimary = Color(0xFFA5C8FF)
val md_theme_light_shadow = Color(0xFF000000)
val md_theme_light_surfaceTint = Color(0xFF005FAF)
val md_theme_light_outlineVariant = Color(0xFFF3F3F4)
val md_theme_light_scrim = Color(0xFF000000)

val md_theme_dark_primary = Color(0xFFFFFFFF)
val md_theme_dark_onPrimary = Color(0xFF00315F)
val md_theme_dark_primaryContainer = Color(0xFF004786)
val md_theme_dark_onPrimaryContainer = Color(0xFFD4E3FF)
val md_theme_dark_secondary = Color(0xFFFFB1C2)
val md_theme_dark_onSecondary = Color(0xFF66002B)
val md_theme_dark_secondaryContainer = Color(0xFF8F003F)
val md_theme_dark_onSecondaryContainer = Color(0xFFFFD9DF)
val md_theme_dark_tertiary = Color(0xFFFFB865)
val md_theme_dark_onTertiary = Color(0xFF482A00)
val md_theme_dark_tertiaryContainer = Color(0xFF663D00)
val md_theme_dark_onTertiaryContainer = Color(0xFFFFDDBA)
val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
val md_theme_dark_background = Color(0xFF000000)
val md_theme_dark_onBackground = Color(0xFFFFFFFF)
val md_theme_dark_surface = Color(0xFF19121F)
val md_theme_dark_onSurface = Color(0xFFFFFFFF)
val md_theme_dark_surfaceVariant = Color(0xFF89868C)
val md_theme_dark_onSurfaceVariant = Color(0xFFC3C6CF)
val md_theme_dark_outline = Color(0xFF8D9199)
val md_theme_dark_inverseOnSurface = Color(0xFF001F25)
val md_theme_dark_inverseSurface = Color(0xFFA6EEFF)
val md_theme_dark_inversePrimary = Color(0xFF005FAF)
val md_theme_dark_shadow = Color(0xFF000000)
val md_theme_dark_surfaceTint = Color(0xFFA5C8FF)
val md_theme_dark_outlineVariant = Color(0xFF201925)
val md_theme_dark_scrim = Color(0xFF000000)

val seed = Color(0xFF1976D2)

data class GPTInvestorColors(
    val textColors: TextColors,
    val greenColors: GreenColors,
    val utilColors: UtilColors,
    val accentColors: AccentColors
)

@Immutable
data class TextColors(
    val secondary50: Color,
)

@Immutable
data class GreenColors(
    val defaultGreen: Color,
    val allGreen: Color
)

@Immutable
data class UtilColors(
    val allDark2: Color,
    val borderBright10: Color
)

@Immutable
data class AccentColors(
    val allAccent: Color
)


val gptInvestorColorsDark = GPTInvestorColors(
    textColors = TextColors(
        secondary50 = Color(0xFF89868C),
    ),
    greenColors = GreenColors(
        defaultGreen = Color(0xFF05C702),
        allGreen = Color(0xFF02A400)
    ),
    utilColors = UtilColors(
        allDark2 = Color(0xFF180C25),
        borderBright10 = Color(0xFF2C2531)

    ),
    accentColors = AccentColors(
        allAccent = Color(0xFF3F008B)
    )
)

val gptInvestorColorsLight = GPTInvestorColors(
    textColors = TextColors(
        secondary50 = Color(0xFF89868C),
    ),
    greenColors = GreenColors(
        defaultGreen = Color(0xFF05C702),
        allGreen = Color(0xFF02A400)
    ),
    utilColors = UtilColors(
        allDark2 = Color(0xFFFBFAFD),
        borderBright10 = Color(0xFFE7E7E8)
    ),
    accentColors = AccentColors(
        allAccent = Color(
            0xFF3F008B
        )
    )

)