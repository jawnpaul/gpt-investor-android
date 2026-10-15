package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp

@Composable
fun GiFillScrollColumn(
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .verticalScroll(scrollState),
        content = content
    )
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiFillScrollColumnPreview() {
    GiPreviewSurface {
        GiFillScrollColumn(modifier = Modifier.height(320.dp)) {
            GiText(text = "Pinned to the top", style = GiTextStyle.SectionTitle)
            Spacer(modifier = Modifier.weight(1f))
            GiButton(text = "Pinned to the bottom", onClick = {}, modifier = Modifier.fillMaxWidth())
        }
    }
}
