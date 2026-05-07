package com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.input_logo
import com.thejawnpaul.gptinvestor.sign_in_action
import com.thejawnpaul.gptinvestor.sign_in_to_save_stocks
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.you_re_browsing_as_a_guest
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val bannerPurple = Color(0xFF5B2D8E)

@Composable
fun GuestBanner(onSignIn: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                color = bannerPurple
            ) {
                Image(
                    modifier = Modifier
                        .padding(10.dp)
                        .size(24.dp),
                    painter = painterResource(Res.drawable.input_logo),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = stringResource(Res.string.you_re_browsing_as_a_guest),
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = stringResource(Res.string.sign_in_to_save_stocks),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            OutlinedButton(
                onClick = onSignIn,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
            ) {
                Text(
                    text = stringResource(Res.string.sign_in_action),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun GuestBannerPreview() {
    GPTInvestorTheme {
        Surface {
            GuestBanner(onSignIn = {}, modifier = Modifier.padding(16.dp))
        }
    }
}
