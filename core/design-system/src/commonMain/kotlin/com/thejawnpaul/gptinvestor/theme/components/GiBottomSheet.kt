package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiElevation
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import kotlinx.coroutines.launch

enum class GiSheetSubtitle { Beside, Caption, Below }

// The close button stays even though dragging works.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GiBottomSheet(
    onDismissRequest: () -> Unit,
    title: String,
    closeContentDescription: String,
    modifier: Modifier = Modifier,
    eyebrow: (@Composable () -> Unit)? = null,
    subtitle: String? = null,
    subtitleStyle: GiSheetSubtitle = GiSheetSubtitle.Beside,
    sheetState: SheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
    ),
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = GiTheme.colors
    val scope = rememberCoroutineScope()
    val maxHeight =
        with(LocalDensity.current) { (LocalWindowInfo.current.containerSize.height * MAX_HEIGHT_FRACTION).toDp() }
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier.dropShadow(shape = GiRadius.sheet, shadow = GiElevation.overlay(colors.isDark)),
        sheetState = sheetState,
        shape = GiRadius.sheet,
        containerColor = colors.raised,
        contentColor = colors.ink,
        tonalElevation = 0.dp,
        scrimColor = colors.scrim,
        dragHandle = null,
        contentWindowInsets = { WindowInsets(0) }
    ) {
        GiBottomSheetContent(
            title = title,
            closeContentDescription = closeContentDescription,
            eyebrow = eyebrow,
            subtitle = subtitle,
            subtitleStyle = subtitleStyle,
            onClose = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) onDismissRequest()
                }
            },
            modifier = Modifier
                .heightIn(max = maxHeight)
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.navigationBars),
            content = content
        )
    }
}

@Composable
private fun GiBottomSheetContent(
    title: String,
    closeContentDescription: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    eyebrow: (@Composable () -> Unit)? = null,
    subtitle: String? = null,
    subtitleStyle: GiSheetSubtitle = GiSheetSubtitle.Beside,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = GiSpace.gutter, end = GiSpace.gutter, top = 10.dp, bottom = GiSpace.x6),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x4)
    ) {
        GiSheetHandle(modifier = Modifier.align(Alignment.CenterHorizontally))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
            verticalAlignment = if (eyebrow == null && (subtitle == null || subtitleStyle != GiSheetSubtitle.Beside)) {
                Alignment.CenterVertically
            } else {
                Alignment.Top
            }
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(
                    if (subtitleStyle == GiSheetSubtitle.Caption) 0.dp else GiSpace.x2
                )
            ) {
                eyebrow?.invoke()
                GiText(text = title, modifier = Modifier.semantics { heading() }, style = GiTextStyle.StatNumber)
                if (subtitle != null && subtitleStyle == GiSheetSubtitle.Beside) {
                    GiText(text = subtitle, style = GiTextStyle.Body, color = GiTheme.colors.body)
                } else if (subtitle != null && subtitleStyle == GiSheetSubtitle.Caption) {
                    GiText(text = subtitle, style = GiTextStyle.MetaRelaxed, color = GiTheme.colors.muted)
                }
            }
            GiIconButton(
                icon = GiIcons.x,
                contentDescription = closeContentDescription,
                onClick = onClose,
                variant = GiIconButtonVariant.Default
            )
        }
        if (subtitle != null && subtitleStyle == GiSheetSubtitle.Below) {
            GiText(
                text = subtitle,
                modifier = Modifier.layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    val pull = GiSpace.x2.roundToPx()
                    layout(placeable.width, placeable.height - pull) { placeable.place(0, -pull) }
                },
                style = GiTextStyle.Body,
                color = GiTheme.colors.body
            )
        }
        content()
    }
}

private const val MAX_HEIGHT_FRACTION = 0.9f

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiBottomSheetContentPreview() {
    GiPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x4)) {
            Column(
                modifier = Modifier
                    .clip(GiRadius.sheet)
                    .background(GiTheme.colors.raised)
            ) {
                GiBottomSheetContent(title = "Set a price alert", closeContentDescription = "Close", onClose = {}) {
                    GiText(text = "We'll tell you when Apple crosses your price.", style = GiTextStyle.BodySmall)
                    GiButton(text = "Set alert", onClick = {}, modifier = Modifier.fillMaxWidth())
                }
            }
            Column(
                modifier = Modifier
                    .clip(GiRadius.sheet)
                    .background(GiTheme.colors.raised)
            ) {
                GiBottomSheetContent(
                    title = "Go Premium",
                    closeContentDescription = "Close",
                    onClose = {},
                    eyebrow = { GiProTag(tone = GiProTagTone.Standalone) },
                    subtitle = "Unlock the full GPT Investor experience."
                ) {
                    GiButton(text = "Subscribe now", onClick = {}, modifier = Modifier.fillMaxWidth(), proPulse = true)
                }
            }
        }
    }
}
