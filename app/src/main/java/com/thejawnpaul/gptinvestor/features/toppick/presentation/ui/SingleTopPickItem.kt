package com.thejawnpaul.gptinvestor.features.toppick.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thejawnpaul.gptinvestor.features.toppick.presentation.model.TopPickPresentation
import com.thejawnpaul.gptinvestor.ui.theme.GPTInvestorTheme

@Composable
fun SingleTopPickItem(modifier: Modifier = Modifier, pickPresentation: TopPickPresentation, onClick: (Long) -> Unit) {
    OutlinedCard(
        modifier = Modifier.padding(horizontal = 16.dp),
        onClick = { onClick(pickPresentation.id) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(60.dp)

        ) {
            Text(
                text = pickPresentation.companyName,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                ),
                maxLines = 1,
                modifier = Modifier.align(Alignment.TopStart)
            )

            Text(
                text = pickPresentation.rationale,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
    }
}

@Preview
@Composable
fun SinglePickPreview(modifier: Modifier = Modifier) {
    GPTInvestorTheme {
        Surface {
            Column(modifier = Modifier.fillMaxSize()) {
                val pick = TopPickPresentation(
                    id = 1,
                    ticker = "AAPL",
                    companyName = "Microsoft",
                    rationale = "This is the day that the Lord has made I will be glad and rejoice in it because, oh well it is just blah lorem ipsum ",
                    confidenceScore = 2,
                    metrics = emptyList(),
                    risks = emptyList()
                )

                SingleTopPickItem(modifier = Modifier, pickPresentation = pick) { }
            }
        }
    }
}
