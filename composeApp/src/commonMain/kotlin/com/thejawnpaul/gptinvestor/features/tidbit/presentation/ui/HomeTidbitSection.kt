package com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.arrow_right
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.state.HomeTidbitView
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import com.thejawnpaul.gptinvestor.tidbit_min_read
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeTidbitSection(tidbit: HomeTidbitView, onClick: (String) -> Unit, modifier: Modifier = Modifier) {
    val gptInvestorColors = LocalGPTInvestorColors.current

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        onClick = { onClick(tidbit.id) }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(52.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                AsyncImage(
                    model = tidbit.previewUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(Res.string.tidbit_min_read),
                    style = MaterialTheme.typography.labelSmall,
                    color = gptInvestorColors.textColors.secondary50
                )
                Text(
                    text = tidbit.title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                modifier = Modifier.size(16.dp),
                painter = painterResource(Res.drawable.arrow_right),
                contentDescription = null,
                tint = gptInvestorColors.textColors.secondary50
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun HomeTidbitSectionPreview() {
    GPTInvestorTheme {
        HomeTidbitSection(
            tidbit = HomeTidbitView(
                id = "1",
                previewUrl = "",
                title = "When good traits lead to bad outcomes",
                description = "Learn how compounding works and why it is the key to wealth creation."
            ),
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
