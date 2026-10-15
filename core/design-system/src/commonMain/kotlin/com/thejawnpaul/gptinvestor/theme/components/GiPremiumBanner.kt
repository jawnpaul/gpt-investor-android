package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

@Composable
fun GiPremiumBanner(
    title: String,
    subtitle: String,
    actionText: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLargeText = isLargeText()
    val bannerModifier = modifier
        .fillMaxWidth()
        .clip(GiRadius.md)
        .background(GiTheme.colors.violetSoft)
        .padding(horizontal = GiSpace.x4, vertical = GiSpace.x3)
    val copy = @Composable { copyModifier: Modifier ->
        Column(modifier = copyModifier, verticalArrangement = Arrangement.spacedBy(GiSpace.x1)) {
            GiProTag()
            GiText(text = title, style = GiTextStyle.Link, color = GiTheme.colors.ink)
            GiText(text = subtitle, style = GiTextStyle.Caption)
        }
    }
    val action = @Composable {
        GiButton(text = actionText, onClick = onActionClick, size = GiButtonSize.Small)
    }
    if (isLargeText) {
        Column(modifier = bannerModifier, verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
            copy(Modifier)
            action()
        }
    } else {
        Row(
            modifier = bannerModifier,
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
            verticalAlignment = Alignment.CenterVertically
        ) {
            copy(Modifier.weight(1f))
            action()
        }
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiPremiumBannerPreview() {
    GiPreviewSurface {
        GiPremiumBanner(
            title = "NVDA, MSFT, GOOGL & AMZN locked",
            subtitle = "Full summaries with Premium",
            actionText = "Unlock",
            onActionClick = {}
        )
    }
}
