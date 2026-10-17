package com.thejawnpaul.gptinvestor.features.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.back
import com.thejawnpaul.gptinvestor.close
import com.thejawnpaul.gptinvestor.delete_account_body
import com.thejawnpaul.gptinvestor.delete_account_title
import com.thejawnpaul.gptinvestor.digest_delivery_time
import com.thejawnpaul.gptinvestor.digest_picker_body
import com.thejawnpaul.gptinvestor.digest_picker_hour
import com.thejawnpaul.gptinvestor.digest_picker_hour_clock
import com.thejawnpaul.gptinvestor.digest_picker_minute
import com.thejawnpaul.gptinvestor.digest_picker_save
import com.thejawnpaul.gptinvestor.features.profile.presentation.ui.AccountSection
import com.thejawnpaul.gptinvestor.features.profile.presentation.ui.AccountSettingsRow
import com.thejawnpaul.gptinvestor.features.settings.presentation.state.SettingsView
import com.thejawnpaul.gptinvestor.keep_my_account
import com.thejawnpaul.gptinvestor.morning_brief
import com.thejawnpaul.gptinvestor.settings
import com.thejawnpaul.gptinvestor.settings_account
import com.thejawnpaul.gptinvestor.settings_delete_account
import com.thejawnpaul.gptinvestor.settings_delete_footnote
import com.thejawnpaul.gptinvestor.settings_digest_footnote
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiBottomSheet
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiDialog
import com.thejawnpaul.gptinvestor.theme.components.GiHaptics
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle
import com.thejawnpaul.gptinvestor.theme.components.GiTopBar
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

private const val DEFAULT_DIGEST_HOUR = 7

@Composable
fun SettingsScreen(
    state: SettingsView,
    onEvent: (SettingsEvent) -> Unit,
    onAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GiTheme.colors.bg)
    ) {
        GiTopBar(
            title = stringResource(Res.string.settings),
            onBack = { onAction(SettingsAction.OnGoBack) },
            backContentDescription = stringResource(Res.string.back)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = GiSpace.gutter, end = GiSpace.gutter, top = GiSpace.x3, bottom = GiSpace.sectionGap),
            verticalArrangement = Arrangement.spacedBy(GiSpace.sectionGap)
        ) {
            AccountSection(
                title = stringResource(Res.string.morning_brief),
                footnote = stringResource(Res.string.settings_digest_footnote)
            ) {
                AccountSettingsRow(
                    icon = GiIcons.clock,
                    label = stringResource(Res.string.digest_delivery_time),
                    value = state.digestDeliveryHourLabel,
                    onClick = { onEvent(SettingsEvent.ShowTimePicker) }
                )
            }
            AccountSection(
                title = stringResource(Res.string.settings_account),
                footnote = stringResource(Res.string.settings_delete_footnote)
            ) {
                AccountSettingsRow(
                    icon = GiIcons.trash,
                    label = stringResource(Res.string.settings_delete_account),
                    showCaret = false,
                    destructive = true,
                    onClick = { onEvent(SettingsEvent.ShowDeleteDialog) }
                )
            }
        }
    }

    if (state.showDeleteDialog) {
        GiDialog(
            title = stringResource(Res.string.delete_account_title),
            body = stringResource(Res.string.delete_account_body),
            confirmText = stringResource(Res.string.settings_delete_account),
            onConfirm = {
                haptics.performHapticFeedback(GiHaptics.Reject)
                onEvent(SettingsEvent.DeleteAccount)
            },
            dismissText = stringResource(Res.string.keep_my_account),
            onDismiss = { onEvent(SettingsEvent.DismissDeleteDialog) },
            icon = GiIcons.trash,
            destructive = true
        )
    }

    if (state.showTimePicker) {
        DigestTimePickerSheet(
            currentHour = state.digestDeliveryHour,
            onSave = { onEvent(SettingsEvent.UpdateDigestTime(it)) },
            onDismiss = { onEvent(SettingsEvent.DismissTimePicker) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DigestTimePickerSheet(currentHour: Int?, onSave: (Int) -> Unit, onDismiss: () -> Unit) {
    var hour by remember(currentHour) { mutableIntStateOf(currentHour ?: DEFAULT_DIGEST_HOUR) }
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
    )
    GiBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        title = stringResource(Res.string.digest_delivery_time),
        closeContentDescription = stringResource(Res.string.close),
        subtitle = stringResource(Res.string.digest_picker_body)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x2, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TimeBox(value = hour, label = stringResource(Res.string.digest_picker_hour), active = true)
            Text(
                text = ":",
                modifier = Modifier.clearAndSetSemantics {},
                style = GiTheme.type.hero,
                color = GiTheme.colors.muted
            )
            TimeBox(value = 0, label = stringResource(Res.string.digest_picker_minute), active = false)
        }
        HourDial(
            selectedHour = hour,
            onSelect = { hour = it },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        GiButton(
            text = stringResource(Res.string.digest_picker_save),
            onClick = {
                haptics.performHapticFeedback(GiHaptics.Confirm)
                scope.launch { sheetState.hide() }.invokeOnCompletion { onSave(hour) }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private val TIME_BOX_WIDTH = 96.dp
private val TIME_BOX_HEIGHT = 72.dp

@Composable
private fun TimeBox(value: Int, label: String, active: Boolean) {
    val colors = GiTheme.colors
    val text = value.toString().padStart(2, '0')
    Box(
        modifier = Modifier
            .size(width = TIME_BOX_WIDTH, height = TIME_BOX_HEIGHT)
            .clip(GiRadius.sm)
            .background(if (active) colors.violetSoft else colors.raised)
            .border(GiEdge.width, if (active) colors.violet else colors.line, GiRadius.sm)
            .clearAndSetSemantics { contentDescription = "$label $text" },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = GiTheme.type.hero.merge(TextStyle(fontFeatureSettings = "tnum")),
            color = if (active) colors.violet else colors.ink,
            maxLines = 1
        )
    }
}

private val DIAL_SIZE = 256.dp
private val OUTER_RADIUS = 100.dp
private val INNER_RADIUS = 64.dp
private val OUTER_LABEL = 40.dp
private val INNER_LABEL = 32.dp
private val HAND_WIDTH = 2.dp
private val CENTER_DOT = 8.dp
private const val HOURS_PER_RING = 12
private const val DEGREES_PER_HOUR = 30.0

private fun ringIndexOf(hour: Int) = hour % HOURS_PER_RING
private fun isOuter(hour: Int) = hour in 1..12

@Composable
private fun HourDial(selectedHour: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    val colors = GiTheme.colors
    val haptics = LocalHapticFeedback.current
    val select by rememberUpdatedState { hour: Int ->
        if (hour != selectedHour) haptics.performHapticFeedback(GiHaptics.Selection)
        onSelect(hour)
    }
    val handLength = if (isOuter(selectedHour)) OUTER_RADIUS - OUTER_LABEL / 2 else INNER_RADIUS - INNER_LABEL / 2
    val dialLabel = stringResource(Res.string.digest_picker_hour_clock)
    Box(
        modifier = modifier
            .size(DIAL_SIZE)
            .clip(GiRadius.full)
            .background(colors.tile)
            .drawBehind {
                val center = Offset(size.width / 2f, size.height / 2f)
                val angle = ringIndexOf(selectedHour) * DEGREES_PER_HOUR * PI / 180.0
                val end = Offset(
                    center.x + (handLength.toPx() * sin(angle)).toFloat(),
                    center.y - (handLength.toPx() * cos(angle)).toFloat()
                )
                drawLine(color = colors.violet, start = center, end = end, strokeWidth = HAND_WIDTH.toPx())
                drawCircle(color = colors.violet, radius = CENTER_DOT.toPx() / 2f, center = center)
            }
            .semantics { contentDescription = dialLabel }
            .selectableGroup()
            .pointerInput(Unit) {
                detectTapGestures { tap ->
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val dx = tap.x - center.x
                    val dy = tap.y - center.y
                    val distance = hypot(dx, dy)
                    val degrees = (atan2(dx, -dy) * 180.0 / PI + 360.0) % 360.0
                    val index = ((degrees / DEGREES_PER_HOUR).roundToInt()) % HOURS_PER_RING
                    val outer = distance > (OUTER_RADIUS + INNER_RADIUS).toPx() / 2f
                    select(
                        when {
                            outer -> if (index == 0) HOURS_PER_RING else index
                            else -> if (index == 0) 0 else index + HOURS_PER_RING
                        }
                    )
                }
            }
    ) {
        for (index in 0 until HOURS_PER_RING) {
            val outerHour = if (index == 0) HOURS_PER_RING else index
            val innerHour = if (index == 0) 0 else index + HOURS_PER_RING
            DialLabel(
                hour = outerHour,
                index = index,
                radius = OUTER_RADIUS,
                labelSize = OUTER_LABEL,
                outer = true,
                selected = selectedHour == outerHour,
                onClick = { select(outerHour) }
            )
            DialLabel(
                hour = innerHour,
                index = index,
                radius = INNER_RADIUS,
                labelSize = INNER_LABEL,
                outer = false,
                selected = selectedHour == innerHour,
                onClick = { select(innerHour) }
            )
        }
    }
}

@Composable
private fun DialLabel(
    hour: Int,
    index: Int,
    radius: Dp,
    labelSize: Dp,
    outer: Boolean,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = GiTheme.colors
    val text = hour.toString().padStart(2, '0')
    val angle = index * DEGREES_PER_HOUR * PI / 180.0
    Box(
        modifier = Modifier
            .offset {
                val center = (DIAL_SIZE / 2).toPx()
                val r = radius.toPx()
                val half = (labelSize / 2).toPx()
                IntOffset(
                    (center + r * sin(angle) - half).roundToInt(),
                    (center - r * cos(angle) - half).roundToInt()
                )
            }
            .size(labelSize)
            .clip(GiRadius.full)
            .background(if (selected) colors.violet else Color.Transparent)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        GiText(
            text = text,
            style = when {
                selected && outer -> GiTextStyle.Label
                outer -> GiTextStyle.LabelRegular
                else -> GiTextStyle.NavLabel
            },
            color = when {
                selected -> colors.onViolet
                outer -> colors.ink
                else -> colors.muted
            },
            maxLines = 1
        )
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun SettingsScreenPreview() {
    GPTInvestorTheme {
        SettingsScreen(
            state = SettingsView(digestDeliveryHour = 7, digestDeliveryHourLabel = "07:00"),
            onEvent = {},
            onAction = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun SettingsTimePickerPreview() {
    GPTInvestorTheme {
        SettingsScreen(
            state = SettingsView(digestDeliveryHour = 7, digestDeliveryHourLabel = "07:00", showTimePicker = true),
            onEvent = {},
            onAction = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun SettingsDeleteConfirmPreview() {
    GPTInvestorTheme {
        SettingsScreen(
            state = SettingsView(digestDeliveryHour = 7, digestDeliveryHourLabel = "07:00", showDeleteDialog = true),
            onEvent = {},
            onAction = {}
        )
    }
}
