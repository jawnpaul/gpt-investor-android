package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

enum class GiBannerTone { Violet, Caution, Error }

@Composable
fun GiBanner(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null,
    leading: (@Composable () -> Unit)? = null,
    tone: GiBannerTone = GiBannerTone.Violet,
    onClick: (() -> Unit)? = null,
    subtitleStyle: GiTextStyle = GiTextStyle.Caption,
    subtitleColor: Color = GiTheme.colors.body,
    action: (@Composable () -> Unit)? = null
) {
    val colors = GiTheme.colors
    val toneColors = when (tone) {
        GiBannerTone.Violet -> GiBannerColors(container = colors.violetSoft, accent = colors.violet)
        GiBannerTone.Caution -> GiBannerColors(container = colors.cautionSoft, accent = colors.caution)
        GiBannerTone.Error -> GiBannerColors(container = colors.errorSoft, accent = colors.error)
    }
    val isLargeText = isLargeText()
    val bannerModifier = modifier
        .fillMaxWidth()
        .clip(GiRadius.md)
        .background(toneColors.container)
        .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier)
        .padding(horizontal = GiSpace.x4, vertical = GiSpace.x3)
    val copy = @Composable { copyModifier: Modifier ->
        Row(
            modifier = copyModifier,
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
            verticalAlignment = Alignment.Top
        ) {
            if (leading != null) {
                leading()
            } else if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(GiIconSize.md),
                    tint = toneColors.accent
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(GiSpace.x0_5)) {
                GiText(text = title, style = GiTextStyle.Link, color = colors.ink)
                if (subtitle != null) {
                    GiText(text = subtitle, style = subtitleStyle, color = subtitleColor)
                }
            }
        }
    }
    if (action != null && isLargeText) {
        Column(modifier = bannerModifier, verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
            copy(Modifier.fillMaxWidth())
            action()
        }
    } else {
        Row(
            modifier = bannerModifier,
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
            verticalAlignment = Alignment.Top
        ) {
            copy(Modifier.weight(1f))
            action?.invoke()
        }
    }
}

private data class GiBannerColors(val container: Color, val accent: Color)

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiBannerPreview() {
    GiPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
            GiBanner(title = "Next brief by 7:00", icon = GiIcons.clock)
            GiBanner(
                title = "Get it the moment it's ready",
                subtitle = "Premium sends a notification",
                icon = GiIcons.bell,
                onClick = {}
            )
            GiBanner(
                title = "Couldn't load",
                subtitle = "Live prices are unavailable right now",
                tone = GiBannerTone.Error
            ) {
                GiButton(text = "Retry", onClick = {}, size = GiButtonSize.Small, variant = GiButtonVariant.Secondary)
            }
            GiBanner(
                title = "You're offline",
                subtitle = "Prices from 07:42",
                icon = GiIcons.wifiSlash,
                tone = GiBannerTone.Caution
            ) {
                GiButton(text = "Retry", onClick = {}, size = GiButtonSize.Small, variant = GiButtonVariant.Caution)
            }
            GiBanner(
                title = "You're browsing as a guest",
                subtitle = "Sign in to save stocks and track performance",
                leading = { GiIconTile(icon = GiIcons.mark, tone = GiIconTileTone.Raised) }
            ) {
                GiButton(text = "Sign in", onClick = {}, size = GiButtonSize.Small, variant = GiButtonVariant.Secondary)
            }
        }
    }
}
