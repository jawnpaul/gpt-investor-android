package com.thejawnpaul.gptinvestor.features.search.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.ask_ai_icon
import com.thejawnpaul.gptinvestor.ic_arrow_right
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import org.jetbrains.compose.resources.painterResource

@Composable
fun SearchAskGptRow(title: String, query: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(Res.drawable.ask_ai_icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "\"$query\"",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_right),
                contentDescription = null,
                modifier = Modifier.size(16.dp).rotate(-45f),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun SearchAskGptRowPreview() {
    GPTInvestorTheme {
        SearchAskGptRow(
            title = "Ask GPT Investor",
            query = "Is Tesla overvalued?",
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
