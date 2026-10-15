package com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.close
import com.thejawnpaul.gptinvestor.features.company.domain.model.BriefSentiment
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiBottomSheet
import com.thejawnpaul.gptinvestor.theme.components.GiSentimentPill
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyBriefSentimentSheet(
    sentiment: BriefSentiment,
    summary: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val label = stringResource(sentiment.labelRes())
    GiBottomSheet(
        onDismissRequest = onDismiss,
        title = label,
        closeContentDescription = stringResource(Res.string.close),
        modifier = modifier
    ) {
        GiSentimentPill(sentiment = sentiment.toGi(), label = label)
        summary?.let { Text(text = it, color = GiTheme.colors.body, style = GiTheme.type.body) }
    }
}
