package com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.read_more
import com.thejawnpaul.gptinvestor.show_less
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import org.jetbrains.compose.resources.stringResource

private const val COLLAPSED_THRESHOLD = 240
private const val COLLAPSED_TAKE = 200

@Composable
fun BriefExpandableText(text: String, modifier: Modifier = Modifier) {
    var expanded by remember(text) { mutableStateOf(false) }
    val canCollapse = text.length > COLLAPSED_THRESHOLD
    val visible = if (!canCollapse || expanded) {
        text
    } else {
        text.take(COLLAPSED_TAKE).trimEnd { it.isWhitespace() || it == '.' || it == ',' } + "…"
    }

    val bodyStyle = MaterialTheme.typography.bodyLarge.copy(
        color = MaterialTheme.colorScheme.onBackground
    )
    val accent = LocalGPTInvestorColors.current.accentColors.allAccent

    Column(modifier = modifier) {
        Text(
            text = visible,
            style = bodyStyle
        )
        if (canCollapse) {
            Text(
                text = stringResource(if (expanded) Res.string.show_less else Res.string.read_more),
                style = MaterialTheme.typography.labelLarge,
                color = accent,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { expanded = !expanded }
                    )
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun BriefExpandableTextPreview() {
    GPTInvestorTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(24.dp)
            ) {
                Column {
                    Text("Short text:", style = MaterialTheme.typography.labelSmall)
                    BriefExpandableText(text = "This is a short text that doesn't need expanding.")
                }

                Column {
                    Text("Long text:", style = MaterialTheme.typography.labelSmall)
                    BriefExpandableText(
                        text = """
                        Apple is the world's largest consumer-electronics company, best known for the iPhone. 
                        Hardware sales are flat year-over-year, but its services business — App Store, 
                        iCloud, Apple Music — is steadily growing. 
                        
                        The company's focus on privacy and ecosystem integration remains a key competitive advantage. 
                        However, regulatory scrutiny in the EU and US regarding App Store practices poses a potential risk to future services revenue growth. 
                        Despite these challenges, Apple's strong balance sheet and consistent share buybacks continue to attract long-term investors.
                        """.trimIndent()
                    )
                }
            }
        }
    }
}
