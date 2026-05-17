package com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.baseline_error_outline_24
import com.thejawnpaul.gptinvestor.couldn_t_load
import com.thejawnpaul.gptinvestor.retry
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeErrorCard(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    val gptInvestorColors = LocalGPTInvestorColors.current
    val errorColor = gptInvestorColors.redColors.allRed

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, errorColor.copy(alpha = 0.4f)),
        color = errorColor.copy(alpha = 0.08f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = errorColor.copy(alpha = 0.15f)
            ) {
                Icon(
                    modifier = Modifier.padding(6.dp),
                    painter = painterResource(Res.drawable.baseline_error_outline_24),
                    contentDescription = null,
                    tint = errorColor
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = stringResource(Res.string.couldn_t_load),
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            OutlinedButton(
                onClick = onRetry,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, errorColor),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = errorColor)
            ) {
                Text(
                    text = stringResource(Res.string.retry),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun HomeErrorCardPreview() {
    GPTInvestorTheme {
        Surface {
            HomeErrorCard(
                message = "Live prices are unavailable right now",
                onRetry = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
