package com.thejawnpaul.gptinvestor.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

data class GPTInvestorColors(
    val textColors: TextColors,
    val greenColors: GreenColors,
    val utilColors: UtilColors,
    val accentColors: AccentColors,
    val redColors: RedColors
)

@Immutable
data class TextColors(
    val secondary50: Color,
)

@Immutable
data class GreenColors(
    val defaultGreen: Color,
    val allGreen: Color,
    val allGreen10: Color
)

@Immutable
data class UtilColors(
    val allDark2: Color,
    val borderBright10: Color
)

@Immutable
data class AccentColors(
    val allAccent: Color,
    val allAccent20: Color
)

@Immutable
data class RedColors(
    val allRed: Color
)


// Legacy tokens kept for screens not yet rebuilt in v4; each maps to its palette v3 equivalent.
val gptInvestorColorsDark = giDarkColors.toLegacyColors()

val gptInvestorColorsLight = giLightColors.toLegacyColors()

private fun GiColors.toLegacyColors() = GPTInvestorColors(
    textColors = TextColors(
        secondary50 = muted
    ),
    greenColors = GreenColors(
        defaultGreen = gain,
        allGreen = gain,
        allGreen10 = gainSoft
    ),
    utilColors = UtilColors(
        allDark2 = tile,
        borderBright10 = line
    ),
    accentColors = AccentColors(
        allAccent = violet,
        allAccent20 = violetSoft
    ),
    redColors = RedColors(
        allRed = loss
    )
)
