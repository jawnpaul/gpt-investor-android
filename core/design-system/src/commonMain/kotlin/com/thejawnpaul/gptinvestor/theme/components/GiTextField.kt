package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
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
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: ImageVector? = null,
    isPassword: Boolean = false,
    showPasswordContentDescription: String? = null,
    hidePasswordContentDescription: String? = null,
    errorLink: GiLink? = null,
    onFocusLost: (() -> Unit)? = null
) {
    val colors = GiTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isError = errorText != null
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var hasBeenFocused by remember { mutableStateOf(false) }
    val currentOnFocusLost by rememberUpdatedState(onFocusLost)
    val edge = when {
        isError -> GiFieldEdge(color = colors.error, width = GiEdge.focusWidth)
        isFocused -> GiFieldEdge(color = colors.violet, width = GiEdge.focusWidth)
        else -> GiFieldEdge(color = colors.control, width = GiEdge.fieldWidth)
    }
    val edgeColor by animateColorAsState(edge.color, tween(GiMotion.FAST, easing = EaseOutQuart))
    val edgeWidth by animateDpAsState(edge.width, tween(GiMotion.FAST, easing = EaseOutQuart))
    val message = when {
        errorText != null -> GiFieldMessageContent(text = errorText, isError = true, link = errorLink)
        helperText != null -> GiFieldMessageContent(text = helperText, isError = false, link = null)
        else -> null
    }
    Column(modifier = modifier.alpha(if (enabled) 1f else GiMotion.DISABLED_ALPHA)) {
        GiText(text = label, modifier = Modifier.padding(bottom = GiSpace.x2), style = GiTextStyle.FieldLabel)
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { state ->
                    if (state.isFocused) {
                        hasBeenFocused = true
                    } else if (hasBeenFocused) {
                        hasBeenFocused = false
                        currentOnFocusLost?.invoke()
                    }
                }
                .semantics { if (errorText != null) error(errorText) },
            enabled = enabled,
            textStyle = GiTheme.type.input.copy(color = colors.ink),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else visualTransformation,
            singleLine = true,
            interactionSource = interactionSource,
            cursorBrush = SolidColor(colors.violet),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .heightIn(min = 52.dp)
                        .clip(GiRadius.sm)
                        .background(colors.raised)
                        .border(edgeWidth, edgeColor, GiRadius.sm)
                        .padding(start = GiSpace.x4, end = if (isPassword) GiSpace.x1 else GiSpace.x4),
                    horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (leadingIcon != null) {
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = colors.muted
                        )
                    }
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                        if (value.isEmpty() && placeholder != null) {
                            GiText(text = placeholder, style = GiTextStyle.Input, color = colors.muted, maxLines = 1)
                        }
                        innerTextField()
                    }
                    if (isPassword) {
                        GiPasswordToggle(
                            visible = passwordVisible,
                            onVisibleChange = { passwordVisible = it },
                            contentDescription = if (passwordVisible) {
                                hidePasswordContentDescription
                            } else {
                                showPasswordContentDescription
                            },
                            enabled = enabled
                        )
                    }
                }
            }
        )
        AnimatedContent(
            targetState = message,
            transitionSpec = {
                fadeIn(tween(GiMotion.BASE, delayMillis = GiMotion.INSTANT, easing = EaseOutQuart)) togetherWith
                    fadeOut(tween(GiMotion.INSTANT, easing = LinearEasing)) using
                    SizeTransform(clip = false) { _, _ -> GiMotion.smooth() }
            },
            contentKey = { it?.text }
        ) { content ->
            if (content != null) {
                GiFieldMessage(content = content, modifier = Modifier.padding(top = GiSpace.x2))
            }
        }
    }
}

@Composable
private fun GiPasswordToggle(
    visible: Boolean,
    onVisibleChange: (Boolean) -> Unit,
    contentDescription: String?,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val layerAlpha = pressedLayerAlpha(interactionSource)
    val haptics = LocalHapticFeedback.current
    Box(
        modifier = modifier
            .size(GiSpace.touchTarget)
            .clip(GiRadius.full)
            .background(GiTheme.colors.ink.copy(alpha = layerAlpha))
            .toggleable(
                value = visible,
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Switch,
                onValueChange = {
                    haptics.performHapticFeedback(GiHaptics.Selection)
                    onVisibleChange(it)
                }
            )
            .semantics { if (contentDescription != null) this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = visible,
            transitionSpec = {
                fadeIn(tween(GiMotion.FAST, easing = EaseOutQuart)) +
                    scaleIn(GiMotion.snappy(), initialScale = ICON_SWAP_INITIAL_SCALE) togetherWith
                    fadeOut(tween(GiMotion.INSTANT, easing = LinearEasing))
            }
        ) { isVisible ->
            Icon(
                imageVector = if (isVisible) GiIcons.eyeSlash else GiIcons.eye,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = GiTheme.colors.muted
            )
        }
    }
}

@Composable
private fun GiFieldMessage(content: GiFieldMessageContent, modifier: Modifier = Modifier) {
    val (text, isError, link) = content
    val color = if (isError) GiTheme.colors.error else GiTheme.colors.muted
    val firstLineHeight = with(LocalDensity.current) { GiTheme.type.bodySmall.lineHeight.toDp() }
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.Top) {
        if (isError) {
            Box(modifier = Modifier.height(firstLineHeight), contentAlignment = Alignment.Center) {
                Icon(imageVector = GiIcons.warningCircle, contentDescription = null, modifier = Modifier.size(16.dp), tint = color)
            }
        }
        if (link != null) {
            GiLinkText(text = text, links = listOf(link), tone = GiLinkTextTone.Error)
        } else {
            GiText(text = text, style = GiTextStyle.BodySmall, color = color)
        }
    }
}

internal data class GiFieldEdge(val color: Color, val width: Dp)

private data class GiFieldMessageContent(val text: String, val isError: Boolean, val link: GiLink?)

private const val ICON_SWAP_INITIAL_SCALE = 0.8f

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiTextFieldPreview() {
    GiPreviewSurface {
        var email by remember { mutableStateOf("ada@gmail") }
        var password by remember { mutableStateOf("abc") }
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x4)) {
            GiTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                placeholder = "you@example.com",
                errorText = "Enter a valid email",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                leadingIcon = GiIcons.envelopeSimple
            )
            GiTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                helperText = "Use at least 8 characters",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                leadingIcon = GiIcons.lock,
                isPassword = true,
                showPasswordContentDescription = "Show password",
                hidePasswordContentDescription = "Hide password"
            )
            GiTextField(
                value = "",
                onValueChange = {},
                label = "Password",
                placeholder = "Your password",
                errorText = "That email and password don't match. Reset your password.",
                leadingIcon = GiIcons.lock,
                isPassword = true,
                errorLink = GiLink("Reset your password") {}
            )
            GiTextField(value = "", onValueChange = {}, label = "Name", placeholder = "Ada", enabled = false)
        }
    }
}
