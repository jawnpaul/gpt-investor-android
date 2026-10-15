package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

private const val SHIMMER_PERIOD_MS = 1400
private const val SHIMMER_MIN_ALPHA = 0.55f

// Placeholder block in skel; give it a size and the shape of what it stands in for.
@Composable
fun GiSkeleton(modifier: Modifier = Modifier, shape: Shape = GiRadius.sm) {
    val transition = rememberInfiniteTransition()
    val alpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = SHIMMER_MIN_ALPHA,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = SHIMMER_PERIOD_MS / 2, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Box(
        modifier = modifier
            .alpha(alpha)
            .clip(shape)
            .background(GiTheme.colors.skel)
    )
}

@Composable
fun GiSpinner(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier.size(18.dp),
        color = GiTheme.colors.violet,
        strokeWidth = 2.dp
    )
}

@PreviewLightDark
@Composable
private fun GiSkeletonPreview() {
    GiPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
            GiSkeleton(modifier = Modifier.size(40.dp))
            GiSkeleton(modifier = Modifier.fillMaxWidth().height(14.dp))
            GiSkeleton(modifier = Modifier.fillMaxWidth(0.7f).height(14.dp))
            GiSpinner()
        }
    }
}
