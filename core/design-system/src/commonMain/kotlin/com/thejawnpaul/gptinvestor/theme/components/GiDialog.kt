package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiElevation
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSize
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled

@Composable
fun GiDialog(
    title: String,
    body: String,
    confirmText: String,
    onConfirm: () -> Unit,
    dismissText: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = GiIcons.trash,
    destructive: Boolean = true
) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        val visibleState = remember { MutableTransitionState(false).apply { targetState = true } }
        val enter: EnterTransition = fadeIn(tween(GiMotion.BASE)) +
            if (isReducedMotionEnabled()) {
                EnterTransition.None
            } else {
                scaleIn(
                    tween(GiMotion.BASE),
                    initialScale = GiMotion.ENTER_SCALE
                )
            }
        AnimatedVisibility(visibleState = visibleState, enter = enter) {
            GiDialogContent(
                title = title,
                body = body,
                confirmText = confirmText,
                onConfirm = onConfirm,
                dismissText = dismissText,
                onDismiss = onDismiss,
                icon = icon,
                destructive = destructive,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun GiDialogContent(
    title: String,
    body: String,
    confirmText: String,
    onConfirm: () -> Unit,
    dismissText: String,
    onDismiss: () -> Unit,
    icon: ImageVector?,
    destructive: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = GiTheme.colors
    Column(
        modifier = modifier
            .padding(horizontal = GiSpace.gutter)
            .widthIn(max = GiSize.dialogMax)
            .dropShadow(shape = GiRadius.lg, shadow = GiElevation.overlay(colors.isDark))
            .clip(GiRadius.lg)
            .background(colors.raised)
            .border(GiEdge.width, colors.line, GiRadius.lg)
            .padding(GiSpace.x6),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x4)
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(GiRadius.sm)
                    .background(if (destructive) colors.errorSoft else colors.violetSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(GiIconSize.lg),
                    tint = if (destructive) colors.error else colors.violet
                )
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
            GiText(text = title, modifier = Modifier.semantics { heading() }, style = GiTextStyle.StatNumber)
            GiText(text = body, style = GiTextStyle.Body, color = colors.body)
        }
        Column(
            modifier = Modifier.padding(top = GiSpace.x1),
            verticalArrangement = Arrangement.spacedBy(GiSpace.x3)
        ) {
            GiButton(
                text = confirmText,
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
                variant = if (destructive) GiButtonVariant.Destructive else GiButtonVariant.Primary
            )
            GiButton(
                text = dismissText,
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                variant = GiButtonVariant.Secondary
            )
        }
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiDialogPreview() {
    GiPreviewSurface {
        GiDialogContent(
            title = "Delete your account?",
            body = "This removes your history, saved stocks and alerts.",
            confirmText = "Delete account",
            onConfirm = {},
            dismissText = "Keep my account",
            onDismiss = {},
            icon = GiIcons.trash,
            destructive = true
        )
    }
}
