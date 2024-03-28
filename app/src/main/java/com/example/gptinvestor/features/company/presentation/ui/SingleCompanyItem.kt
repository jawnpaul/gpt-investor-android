package com.example.gptinvestor.features.company.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.gptinvestor.R
import com.example.gptinvestor.features.company.presentation.model.CompanyPresentation

@Composable
fun SingleCompanyItem(modifier: Modifier, company: CompanyPresentation, onClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable {
                onClick(company.ticker)
            }
    ) {
        AsyncImage(
            model = company.logo,
            contentDescription = null,
            modifier = Modifier
                .padding(top = 8.dp, start = 16.dp, bottom = 0.dp)
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.baseline_downloading_24),
            error = painterResource(id = R.drawable.baseline_error_outline_24)
        )

        Spacer(modifier = Modifier.size(8.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = company.name,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 4.dp),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = company.ticker,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 4.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
