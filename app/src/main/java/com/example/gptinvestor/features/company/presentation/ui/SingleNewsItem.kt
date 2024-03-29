package com.example.gptinvestor.features.company.presentation.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.gptinvestor.features.company.presentation.model.NewsPresentation

@Composable
fun SingleNewsItem(modifier: Modifier, newsPresentation: NewsPresentation) {
    ElevatedCard(modifier = Modifier.padding(16.dp)) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(bottom = 16.dp, start = 8.dp, end = 8.dp, top = 16.dp),
            shape = RoundedCornerShape(corner = CornerSize(8.dp))
        ) {
            AsyncImage(
                model = newsPresentation.imageUrl,
                contentDescription = "News Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
        }
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = newsPresentation.title,
            style = MaterialTheme.typography.headlineSmall
        )
        Row(modifier = Modifier.padding(start = 8.dp, bottom = 8.dp, top = 16.dp)) {
            Text(
                text = newsPresentation.publisher,
                modifier = Modifier.padding(end = 4.dp)
            )
            Text(
                text = "-",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 2.dp, end = 2.dp)
            )
            Text(
                text = newsPresentation.relativeDate,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
