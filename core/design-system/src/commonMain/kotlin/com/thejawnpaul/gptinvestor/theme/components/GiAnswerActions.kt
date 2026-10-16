package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.theme.GiSpace

enum class GiFeedback { None, Helpful, NotHelpful }

data class GiAnswerActionLabels(val copy: String, val helpful: String, val notHelpful: String, val share: String)

@Composable
fun GiAnswerActions(
    labels: GiAnswerActionLabels,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier,
    feedback: GiFeedback = GiFeedback.None,
    onHelpful: (() -> Unit)? = null,
    onNotHelpful: (() -> Unit)? = null
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(GiSpace.x1)) {
        GiIconButton(
            icon = GiIcons.copy,
            contentDescription = labels.copy,
            onClick = onCopy,
            variant = GiIconButtonVariant.Ghost
        )
        if (onHelpful != null) {
            GiIconToggleButton(
                checked = feedback == GiFeedback.Helpful,
                onCheckedChange = { onHelpful() },
                icon = GiIcons.thumbsUp,
                checkedIcon = GiIcons.thumbsUpFill,
                contentDescription = labels.helpful,
                variant = GiIconButtonVariant.Ghost
            )
        }
        if (onNotHelpful != null) {
            GiIconToggleButton(
                checked = feedback == GiFeedback.NotHelpful,
                onCheckedChange = { onNotHelpful() },
                icon = GiIcons.thumbsDown,
                checkedIcon = GiIcons.thumbsDownFill,
                contentDescription = labels.notHelpful,
                variant = GiIconButtonVariant.Ghost
            )
        }
        GiIconButton(
            icon = GiIcons.shareNetwork,
            contentDescription = labels.share,
            onClick = onShare,
            variant = GiIconButtonVariant.Ghost
        )
    }
}

private val PreviewLabels = GiAnswerActionLabels(
    copy = "Copy",
    helpful = "Helpful",
    notHelpful = "Not helpful",
    share = "Share"
)

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiAnswerActionsPreview() {
    GiPreviewSurface {
        var feedback by remember { mutableStateOf(GiFeedback.Helpful) }
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
            GiAnswerActions(
                labels = PreviewLabels,
                onCopy = {},
                onShare = {},
                feedback = feedback,
                onHelpful = { feedback = GiFeedback.Helpful },
                onNotHelpful = { feedback = GiFeedback.NotHelpful }
            )
            GiAnswerActions(labels = PreviewLabels, onCopy = {}, onShare = {})
        }
    }
}
