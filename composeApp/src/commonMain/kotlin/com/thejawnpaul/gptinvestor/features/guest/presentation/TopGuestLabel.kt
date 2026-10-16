package com.thejawnpaul.gptinvestor.features.guest.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.sign_in_action
import com.thejawnpaul.gptinvestor.sign_in_to_save_stocks
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiBanner
import com.thejawnpaul.gptinvestor.theme.components.GiBannerTone
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiButtonSize
import com.thejawnpaul.gptinvestor.theme.components.GiButtonVariant
import com.thejawnpaul.gptinvestor.theme.components.GiIconTile
import com.thejawnpaul.gptinvestor.theme.components.GiIconTileSize
import com.thejawnpaul.gptinvestor.theme.components.GiIconTileTone
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle
import com.thejawnpaul.gptinvestor.you_re_browsing_as_a_guest
import org.jetbrains.compose.resources.stringResource

@Composable
fun TopGuestLabel(onClick: () -> Unit, modifier: Modifier = Modifier) {
    GiBanner(
        title = stringResource(Res.string.you_re_browsing_as_a_guest),
        modifier = modifier,
        subtitle = stringResource(Res.string.sign_in_to_save_stocks),
        leading = {
            GiIconTile(icon = GiIcons.mark, size = GiIconTileSize.Medium, tone = GiIconTileTone.Raised)
        },
        tone = GiBannerTone.Violet,
        subtitleStyle = GiTextStyle.Meta,
        subtitleColor = GiTheme.colors.body
    ) {
        GiButton(
            text = stringResource(Res.string.sign_in_action),
            onClick = onClick,
            size = GiButtonSize.Small,
            variant = GiButtonVariant.Secondary
        )
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun TopGuestLabelPreview() {
    GPTInvestorTheme {
        TopGuestLabel(onClick = {})
    }
}
