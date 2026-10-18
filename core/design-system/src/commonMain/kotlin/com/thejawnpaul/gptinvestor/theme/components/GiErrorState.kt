package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

@Composable
fun GiErrorState(
    title: String,
    body: String,
    actionText: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = GiIcons.warningCircle
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = GiSpace.gutter),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x3, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(GiIconSize.xl),
            tint = GiTheme.colors.error
        )
        GiText(text = title, style = GiTextStyle.CompanyName, textAlign = TextAlign.Center)
        GiText(text = body, style = GiTextStyle.BodySmall, textAlign = TextAlign.Center)
        GiButton(text = actionText, onClick = onAction, size = GiButtonSize.Medium)
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiErrorStatePreview() {
    GiPreviewSurface {
        GiErrorState(
            title = "Something went wrong",
            body = "We couldn't load this page.",
            actionText = "Try again",
            onAction = {}
        )
    }
}
