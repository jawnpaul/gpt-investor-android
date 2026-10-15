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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
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
    tone: GiBannerTone = GiBannerTone.Violet,
    onClick: (() -> Unit)? = null,
    action: (@Composable () -> Unit)? = null
) {
    val colors = GiTheme.colors
    val (container, accent) = when (tone) {
        GiBannerTone.Violet -> colors.violetSoft to colors.violet
        GiBannerTone.Caution -> colors.cautionSoft to colors.caution
        GiBannerTone.Error -> colors.errorSoft to colors.error
    }
    val isLargeText = isLargeText()
    val bannerModifier = modifier
        .fillMaxWidth()
        .clip(GiRadius.md)
        .background(container)
        .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier)
        .padding(horizontal = GiSpace.x4, vertical = GiSpace.x3)
    val copy = @Composable { copyModifier: Modifier ->
        Row(
            modifier = copyModifier,
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = accent)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                GiText(text = title, style = GiTextStyle.Link, color = colors.ink)
                if (subtitle != null) {
                    GiText(text = subtitle, style = GiTextStyle.Caption)
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            copy(Modifier.weight(1f))
            action?.invoke()
        }
    }
}

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
        }
    }
}
