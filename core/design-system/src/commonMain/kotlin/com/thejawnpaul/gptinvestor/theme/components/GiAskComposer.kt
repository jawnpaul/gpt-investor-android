package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiElevation
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

// Keeps the line hairline on purpose (not the control edge); turns violet while focused or filled.
@Composable
fun GiAskComposer(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    placeholder: String,
    sendContentDescription: String,
    modifier: Modifier = Modifier,
    floating: Boolean = true
) {
    val colors = GiTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val canSend = value.isNotBlank()
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        textStyle = GiTheme.type.body.copy(color = colors.ink),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Send
        ),
        keyboardActions = KeyboardActions(onSend = { if (canSend) onSend() }),
        maxLines = if (isLargeText()) 2 else 1,
        interactionSource = interactionSource,
        cursorBrush = SolidColor(colors.violet),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .then(
                        if (floating) {
                            Modifier.dropShadow(shape = GiRadius.md, shadow = GiElevation.lift(colors.isDark))
                        } else {
                            Modifier
                        }
                    )
                    .heightIn(min = 52.dp)
                    .clip(GiRadius.md)
                    .background(colors.raised)
                    .border(
                        width = GiEdge.width,
                        color = if (isFocused || value.isNotEmpty()) colors.violet else colors.line,
                        shape = GiRadius.md
                    )
                    .padding(start = 14.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = GiIcons.mark,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = colors.violet
                )
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        GiText(
                            text = placeholder,
                            style = GiTextStyle.Body,
                            color = colors.muted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    innerTextField()
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .alpha(if (canSend) 1f else GiMotion.DISABLED_ALPHA)
                        .clip(GiRadius.sm)
                        .background(colors.primary)
                        .clickable(enabled = canSend, role = Role.Button, onClick = onSend)
                        .semantics { contentDescription = sendContentDescription },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = GiIcons.arrowUp,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = colors.onPrimary
                    )
                }
            }
        }
    )
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiAskComposerPreview() {
    GiPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x4)) {
            GiAskComposer(
                value = "",
                onValueChange = {},
                onSend = {},
                placeholder = "Ask me anything about markets…",
                sendContentDescription = "Send"
            )
            GiAskComposer(
                value = "Why is NVDA up today?",
                onValueChange = {},
                onSend = {},
                placeholder = "Ask me anything about markets…",
                sendContentDescription = "Send"
            )
        }
    }
}
