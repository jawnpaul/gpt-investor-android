package com.thejawnpaul.gptinvestor.features.search.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.features.component.ShimmerBox

@Composable
fun SearchLoadingContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(3) {
            ShimmerBox(width = 80.dp, height = 12.dp)
            repeat(3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ShimmerBox(size = 36.dp, shape = CircleShape)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        ShimmerBox(width = 60.dp, height = 14.dp)
                        ShimmerBox(width = 120.dp, height = 12.dp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
