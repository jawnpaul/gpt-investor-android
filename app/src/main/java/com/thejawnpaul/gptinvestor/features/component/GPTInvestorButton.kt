package com.thejawnpaul.gptinvestor.features.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors

@Composable
fun GPTInvestorButton(modifier: Modifier, text: String, enabled: Boolean = true, onClick: () -> Unit) {
    val gptInvestorColors = LocalGPTInvestorColors.current

    Button(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = gptInvestorColors.accentColors.allAccent,
            contentColor = Color.White
        ),
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFFF947F6),
                    Color(0xFF0095FF)
                )
            )
        )
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}

@PreviewLightDark()
@Composable
fun ButtonPreview() {
    GPTInvestorTheme {
        Surface {
            GPTInvestorButton(
                modifier = Modifier,
                text = "Login ",
                onClick = {}
            )
        }
    }
}
