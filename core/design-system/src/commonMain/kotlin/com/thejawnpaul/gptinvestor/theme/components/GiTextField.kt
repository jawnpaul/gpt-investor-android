package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

@Composable
fun GiTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    helperText: String? = null,
    errorText: String? = null,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val colors = GiTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isError = errorText != null
    val edge = when {
        isError -> GiFieldEdge(color = colors.error, width = GiEdge.focusWidth)
        isFocused -> GiFieldEdge(color = colors.violet, width = GiEdge.focusWidth)
        else -> GiFieldEdge(color = colors.control, width = GiEdge.fieldWidth)
    }
    Column(
        modifier = modifier.alpha(if (enabled) 1f else GiMotion.DISABLED_ALPHA),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x2)
    ) {
        GiText(text = label, style = GiTextStyle.FieldLabel)
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { if (errorText != null) error(errorText) },
            enabled = enabled,
            textStyle = GiTheme.type.input.copy(color = colors.ink),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            singleLine = true,
            interactionSource = interactionSource,
            cursorBrush = SolidColor(colors.violet),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .heightIn(min = 52.dp)
                        .clip(GiRadius.sm)
                        .background(colors.raised)
                        .border(edge.width, edge.color, GiRadius.sm)
                        .padding(horizontal = GiSpace.x4),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty() && placeholder != null) {
                        GiText(text = placeholder, style = GiTextStyle.Input, color = colors.muted, maxLines = 1)
                    }
                    innerTextField()
                }
            }
        )
        when {
            errorText != null -> GiFieldMessage(text = errorText, isError = true)
            helperText != null -> GiFieldMessage(text = helperText, isError = false)
        }
    }
}

@Composable
private fun GiFieldMessage(text: String, isError: Boolean) {
    val color = if (isError) GiTheme.colors.error else GiTheme.colors.muted
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        if (isError) {
            Icon(imageVector = GiIcons.warningCircle, contentDescription = null, modifier = Modifier.size(16.dp), tint = color)
        }
        GiText(text = text, style = GiTextStyle.BodySmall, color = color)
    }
}

internal data class GiFieldEdge(val color: Color, val width: Dp)

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiTextFieldPreview() {
    GiPreviewSurface {
        var email by remember { mutableStateOf("ada@gmail") }
        var password by remember { mutableStateOf("") }
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x4)) {
            GiTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                placeholder = "you@example.com",
                errorText = "Enter a valid email",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            GiTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                helperText = "Use at least 8 characters",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation()
            )
            GiTextField(value = "", onValueChange = {}, label = "Name", placeholder = "Ada", enabled = false)
        }
    }
}
