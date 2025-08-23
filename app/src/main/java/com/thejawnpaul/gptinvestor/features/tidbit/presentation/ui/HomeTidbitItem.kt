package com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors

@Composable
fun HomeTidbitItem(modifier: Modifier = Modifier, tidbitId: String, imageUrl: String, title: String, description: String, onTidbitClick: (String) -> Unit) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val gptInvestorColors = LocalGPTInvestorColors.current

        /*Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier,
                text = stringResource(R.string.tidbit)
            )

            Text(
                modifier = Modifier,
                text = stringResource(R.string.see_all)
            )
        }*/

        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onTidbitClick(tidbitId) }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    modifier = Modifier.size(width = 60.dp, height = 66.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    AsyncImage(
                        modifier = Modifier.fillMaxSize(),
                        model = imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = title, maxLines = 1, style = MaterialTheme.typography.bodyLarge)

                    Text(
                        text = description,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium,
                        color = gptInvestorColors.textColors.secondary50
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun HomeTidbitItemPreview() {
    HomeTidbitItem(
        tidbitId = "1",
        imageUrl = "https://www.example.com/image.jpg",
        title = "Understanding Compound Interest",
        description = "Compound interest is the interest on a loan or deposit calculated based on both the initial principal and the accumulated interest from previous periods.",
        onTidbitClick = {}
    )
}
