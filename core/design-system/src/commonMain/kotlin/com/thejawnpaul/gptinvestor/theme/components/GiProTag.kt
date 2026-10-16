package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.em
import com.thejawnpaul.gptinvestor.theme.GiControlHeight
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

enum class GiProTagTone { OnBanner, Standalone }

@Composable
fun GiProTag(modifier: Modifier = Modifier, tone: GiProTagTone = GiProTagTone.OnBanner) {
    Row(
        modifier = modifier
            .heightIn(min = GiControlHeight.pill)
            .clip(GiRadius.xs)
            .background(if (tone == GiProTagTone.Standalone) GiTheme.colors.violetSoft else GiTheme.colors.raised)
            .padding(horizontal = GiSpace.x2_5),
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x1),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = GiIcons.mark,
            contentDescription = null,
            modifier = Modifier.size(GiIconSize.xs),
            tint = GiTheme.colors.violet
        )
        Text(
            text = "PRO",
            style = GiTheme.type.tag.copy(letterSpacing = PRO_LETTER_SPACING.em),
            color = GiTheme.colors.violet,
            maxLines = 1
        )
    }
}

private const val PRO_LETTER_SPACING = 0.04f

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiProTagPreview() {
    GiPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
            Box(modifier = Modifier.background(GiTheme.colors.violetSoft).padding(GiSpace.x3)) {
                GiProTag()
            }
            Box(modifier = Modifier.background(GiTheme.colors.raised).padding(GiSpace.x3)) {
                GiProTag(tone = GiProTagTone.Standalone)
            }
        }
    }
}
