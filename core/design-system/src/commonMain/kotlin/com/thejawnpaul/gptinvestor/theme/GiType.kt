package com.thejawnpaul.gptinvestor.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font

@Immutable
data class GiType(
    val hero: TextStyle,
    val display: TextStyle,
    val screenTitle: TextStyle,
    val pageTitle: TextStyle,
    val statNumber: TextStyle,
    val companyName: TextStyle,
    val sectionTitle: TextStyle,
    val body: TextStyle,
    val bodySmall: TextStyle,
    val label: TextStyle,
    val button: TextStyle,
    val link: TextStyle,
    val meta: TextStyle,
    val eyebrow: TextStyle,
    val delta: TextStyle,
    val tag: TextStyle,
    val caption: TextStyle,
    val navLabel: TextStyle,
    val input: TextStyle,
    val fieldLabel: TextStyle,
)

val LocalGiType = staticCompositionLocalOf<GiType> { error("GiTheme not provided") }

@Composable
fun rememberGiType(): GiType {
    val bricolageMedium = Font(Res.font.bricolage_grotesque_medium, FontWeight.Medium)
    val bricolageSemiBold = Font(Res.font.bricolage_grotesque_semibold, FontWeight.SemiBold)
    val bricolageBold = Font(Res.font.bricolage_grotesque_bold, FontWeight.Bold)
    val jakartaRegular = Font(Res.font.plus_jakarta_sans_regular, FontWeight.Normal)
    val jakartaMedium = Font(Res.font.plus_jakarta_sans_medium, FontWeight.Medium)
    val jakartaSemiBold = Font(Res.font.plus_jakarta_sans_semibold, FontWeight.SemiBold)
    val jakartaBold = Font(Res.font.plus_jakarta_sans_bold, FontWeight.Bold)
    return remember(bricolageSemiBold, jakartaRegular) {
        val display = FontFamily(bricolageMedium, bricolageSemiBold, bricolageBold)
        val sans = FontFamily(jakartaRegular, jakartaMedium, jakartaSemiBold, jakartaBold)

        fun displayStyle(size: Int, lineHeight: Int) = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.SemiBold,
            fontSize = size.sp,
            lineHeight = lineHeight.sp,
            letterSpacing = (-0.02).em,
        )

        fun sansStyle(size: Int, lineHeight: Int, weight: FontWeight) = TextStyle(
            fontFamily = sans,
            fontWeight = weight,
            fontSize = size.sp,
            lineHeight = lineHeight.sp,
        )

        GiType(
            hero = displayStyle(40, 44),
            display = displayStyle(32, 36),
            screenTitle = displayStyle(28, 34),
            pageTitle = displayStyle(24, 30),
            statNumber = displayStyle(20, 26),
            companyName = displayStyle(18, 24),
            sectionTitle = sansStyle(16, 24, FontWeight.SemiBold).copy(letterSpacing = (-0.01).em),
            body = sansStyle(15, 24, FontWeight.Normal),
            bodySmall = sansStyle(14, 20, FontWeight.Normal),
            label = sansStyle(15, 22, FontWeight.SemiBold),
            button = sansStyle(16, 24, FontWeight.SemiBold),
            link = sansStyle(14, 20, FontWeight.SemiBold),
            meta = sansStyle(13, 16, FontWeight.Normal),
            eyebrow = sansStyle(13, 16, FontWeight.Medium),
            delta = sansStyle(13, 20, FontWeight.Medium).copy(fontFeatureSettings = "tnum"),
            tag = sansStyle(12, 16, FontWeight.SemiBold),
            caption = sansStyle(12, 16, FontWeight.Normal),
            navLabel = sansStyle(12, 16, FontWeight.Medium),
            input = sansStyle(16, 24, FontWeight.Normal),
            fieldLabel = sansStyle(13, 18, FontWeight.SemiBold),
        )
    }
}

fun GiType.toTypography(): Typography = Typography(
    displayLarge = hero,
    displayMedium = display,
    displaySmall = screenTitle,
    headlineLarge = screenTitle,
    headlineMedium = statNumber,
    headlineSmall = companyName,
    titleLarge = statNumber,
    titleMedium = sectionTitle,
    titleSmall = label,
    bodyLarge = body,
    bodyMedium = body,
    bodySmall = meta,
    labelLarge = button,
    labelMedium = eyebrow,
    labelSmall = navLabel,
)
