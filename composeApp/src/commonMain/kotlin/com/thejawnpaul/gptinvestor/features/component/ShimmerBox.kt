package com.thejawnpaul.gptinvestor.features.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    width: Dp = Dp.Unspecified,
    height: Dp = Dp.Unspecified,
    shape: Shape = RoundedCornerShape(4.dp)
) {
    Box(
        modifier = modifier
            .clip(shape)
            .then(
                if (!width.isUnspecified && !height.isUnspecified) {
                    Modifier.size(width = width, height = height)
                } else if (!width.isUnspecified) {
                    Modifier.width(width)
                } else if (!height.isUnspecified) {
                    Modifier.height(height)
                } else {
                    Modifier
                }
            )
            .shimmerEffect(
                colorOne = MaterialTheme.colorScheme.surfaceVariant,
                colorTwo = MaterialTheme.colorScheme.outlineVariant
            )
    )
}

@Composable
fun ShimmerBox(size: Dp, modifier: Modifier = Modifier, shape: Shape = RoundedCornerShape(4.dp)) {
    Box(
        modifier = modifier
            .clip(shape)
            .size(size)
            .shimmerEffect(
                colorOne = MaterialTheme.colorScheme.surfaceVariant,
                colorTwo = MaterialTheme.colorScheme.outlineVariant
            )
    )
}

@PreviewLightDark
@Composable
private fun ShimmerBoxPreview() {
    GPTInvestorTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Rectangle shimmer
                ShimmerBox(width = 200.dp, height = 20.dp)

                // Square shimmer
                ShimmerBox(size = 100.dp)

                // Circle shimmer
                ShimmerBox(size = 64.dp, shape = CircleShape)
            }
        }
    }
}
