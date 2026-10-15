package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

@Composable
fun GiProTag(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .heightIn(min = 24.dp)
            .clip(GiRadius.xs)
            .background(GiTheme.colors.raised)
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x1),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = GiIcons.mark,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = GiTheme.colors.violet
        )
        Text(text = "PRO", style = GiTheme.type.tag, color = GiTheme.colors.violet, maxLines = 1)
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiProTagPreview() {
    GiPreviewSurface {
        Box(modifier = Modifier.background(GiTheme.colors.violetSoft).padding(GiSpace.x3)) {
            GiProTag()
        }
    }
}
