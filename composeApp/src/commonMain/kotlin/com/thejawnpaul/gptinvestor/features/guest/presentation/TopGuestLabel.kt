package com.thejawnpaul.gptinvestor.features.guest.presentation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import com.thejawnpaul.gptinvestor.you_re_in_guest_mode_click_here_to_sign_in
import org.jetbrains.compose.resources.stringResource

@Composable
fun TopGuestLabel(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        color = LocalGPTInvestorColors.current.greenColors.defaultGreen
    ) {
        Text(
            modifier = Modifier.padding(2.dp).fillMaxWidth(),
            text = stringResource(Res.string.you_re_in_guest_mode_click_here_to_sign_in),
            textAlign = TextAlign.Center,
            color = Color.White
        )
    }
}

@Preview
@Composable
private fun TopGuestLabelPreview() {
    TopGuestLabel(modifier = Modifier, onClick = {})
}
