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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

// Keeps the line hairline on purpose while idle (the magnifier and placeholder identify it); violet while focused.
@Composable
fun GiSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    clearContentDescription: String,
    modifier: Modifier = Modifier,
    onSearch: (() -> Unit)? = null
) {
    val colors = GiTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val hasText = value.isNotEmpty()
    val edgeColor = when {
        isFocused -> colors.violet
        hasText -> colors.control
        else -> colors.line
    }
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        textStyle = GiTheme.type.input.copy(color = colors.ink),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch?.invoke() }),
        singleLine = true,
        interactionSource = interactionSource,
        cursorBrush = SolidColor(colors.violet),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .clip(GiRadius.sm)
                    .background(colors.raised)
                    .border(GiEdge.width, edgeColor, GiRadius.sm)
                    .padding(start = GiSpace.x4, end = if (hasText) 0.dp else 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = GiIcons.magnifyingGlass,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = colors.muted
                )
                Spacer(modifier = Modifier.width(10.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (!hasText) {
                        GiText(
                            text = placeholder,
                            style = GiTextStyle.Input,
                            color = colors.muted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    innerTextField()
                }
                if (hasText) {
                    // A 48 touch box around the 28 circle puts the circle 10 from the edge, as drawn.
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable(role = Role.Button) { onValueChange("") }
                            .semantics { contentDescription = clearContentDescription },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(GiRadius.full)
                                .background(colors.tile),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = GiIcons.x,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = colors.muted
                            )
                        }
                    }
                }
            }
        }
    )
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiSearchFieldPreview() {
    GiPreviewSurface {
        var query by remember { mutableStateOf("appl") }
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
            GiSearchField(
                value = "",
                onValueChange = {},
                placeholder = "Search stocks or ask",
                clearContentDescription = "Clear search"
            )
            GiSearchField(
                value = query,
                onValueChange = { query = it },
                placeholder = "Search stocks or ask",
                clearContentDescription = "Clear search"
            )
        }
    }
}
