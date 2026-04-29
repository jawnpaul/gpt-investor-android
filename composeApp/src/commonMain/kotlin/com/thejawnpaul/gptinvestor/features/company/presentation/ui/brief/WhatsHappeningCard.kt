package com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.features.company.domain.model.BriefTone
import com.thejawnpaul.gptinvestor.features.company.domain.model.NewsBrief
import com.thejawnpaul.gptinvestor.section_whats_happening
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import com.thejawnpaul.gptinvestor.what_it_means
import org.jetbrains.compose.resources.stringResource

@Composable
fun WhatsHappeningCard(news: List<NewsBrief>, onNewsClick: (link: String) -> Unit, modifier: Modifier = Modifier) {
    BriefCard(modifier = modifier) {
        BriefSectionTitle(text = stringResource(Res.string.section_whats_happening))
        Spacer(Modifier.height(12.dp))
        news.forEachIndexed { index, item ->
            NewsBriefItem(
                item = item,
                onClick = { onNewsClick(item.link) }
            )
            if (index != news.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = briefCardBorderColor()
                )
            }
        }
    }
}

@Composable
private fun NewsBriefItem(item: NewsBrief, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val secondary = LocalGPTInvestorColors.current.textColors.secondary50

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val byline = if (item.publishedRelative.isBlank()) {
            item.publisher
        } else {
            "${item.publisher} · ${item.publishedRelative}"
        }
        Text(
            text = byline,
            style = MaterialTheme.typography.bodySmall,
            color = secondary
        )
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        if (!item.whatItMeans.isNullOrBlank()) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BriefChip(
                    text = stringResource(Res.string.what_it_means),
                    tone = item.tone
                )
                Text(
                    text = item.whatItMeans,
                    style = MaterialTheme.typography.bodyMedium,
                    color = secondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun WhatsHappeningCardPreview() {
    GPTInvestorTheme {
        Surface {
            WhatsHappeningCard(
                modifier = Modifier.padding(16.dp),
                news = listOf(
                    NewsBrief(
                        id = "1",
                        publisher = "Reuters",
                        publishedRelative = "2h ago",
                        title = "Apple unveils on-device AI features for the iPhone, rolling out in iOS 19",
                        whatItMeans = "Could give people a reason to upgrade their phones, lifting revenue.",
                        tone = BriefTone.Positive,
                        link = ""
                    ),
                    NewsBrief(
                        id = "2",
                        publisher = "Bloomberg",
                        publishedRelative = "Yesterday",
                        title = "EU regulators open new probe into App Store fees",
                        whatItMeans = "If Apple has to lower its 30% cut, services revenue would take a small hit.",
                        tone = BriefTone.Negative,
                        link = ""
                    )
                ),
                onNewsClick = {}
            )
        }
    }
}
