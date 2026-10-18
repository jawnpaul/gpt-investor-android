package com.thejawnpaul.gptinvestor.features.profile.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiListGroup
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle

@Composable
internal fun AccountSection(
    title: String,
    modifier: Modifier = Modifier,
    footnote: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
        GiText(
            text = title,
            modifier = Modifier
                .padding(horizontal = GiSpace.x1)
                .semantics { heading() },
            style = GiTextStyle.Eyebrow
        )
        GiListGroup(content = content)
        if (footnote != null) {
            GiText(text = footnote, modifier = Modifier.padding(horizontal = GiSpace.x1), style = GiTextStyle.Meta)
        }
    }
}

@Composable
internal fun AccountSettingsRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    value: String? = null,
    showCaret: Boolean = true,
    destructive: Boolean = false,
    highlighted: Boolean = false
) {
    val colors = GiTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val content = if (destructive) colors.error else colors.ink
    val iconTint = if (destructive) colors.error else colors.muted
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(interactionSource = interactionSource, indication = null, role = Role.Button, onClick = onClick)
            .background(if (highlighted) colors.tile else Color.Transparent)
            .background(if (isPressed) colors.ink.copy(alpha = GiMotion.PRESSED_ALPHA) else Color.Transparent)
            .heightIn(min = GiSpace.settingsRow)
            .padding(horizontal = GiSpace.x4, vertical = GiSpace.x2),
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(GiIconSize.md), tint = iconTint)
        GiText(text = label, modifier = Modifier.weight(1f), style = GiTextStyle.LabelRegular, color = content)
        if (value != null) {
            GiText(text = value, style = GiTextStyle.MetaRelaxed, color = colors.muted, maxLines = 1)
        }
        if (showCaret) {
            Icon(
                imageVector = GiIcons.caretRight,
                contentDescription = null,
                modifier = Modifier.size(GiIconSize.sm),
                tint = colors.muted
            )
        }
    }
}
