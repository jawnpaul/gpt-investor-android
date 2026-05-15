@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.core.navigation.LocalAnimatedVisibilityScope
import com.thejawnpaul.gptinvestor.core.navigation.LocalSharedTransitionScope
import com.thejawnpaul.gptinvestor.features.component.ShimmerBox
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.state.HomeTidbitView
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import com.thejawnpaul.gptinvestor.tidbit_min_read
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeTidbitSection(
    tidbit: HomeTidbitView,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    val gptInvestorColors = LocalGPTInvestorColors.current
    val sharedScope = LocalSharedTransitionScope.current
    val animatedScope = LocalAnimatedVisibilityScope.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "pressScale"
    )

    AnimatedContent(
        targetState = isLoading,
        transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(150)) },
        label = "TidbitSection",
        modifier = modifier
    ) { loading ->
        if (loading) {
            HomeTidbitSectionShimmer()
        } else {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    },
                shape = RoundedCornerShape(16.dp),
                interactionSource = interactionSource,
                onClick = { onClick(tidbit.id) },
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val imageSharedMod = if (sharedScope != null && animatedScope != null) {
                        with(sharedScope) {
                            Modifier.sharedBounds(
                                sharedContentState = rememberSharedContentState("tidbit-image-${tidbit.id}"),
                                animatedVisibilityScope = animatedScope,
                                boundsTransform = { _, _ -> spring(dampingRatio = 0.9f, stiffness = 380f) }
                            )
                        }
                    } else {
                        Modifier
                    }

                    Surface(
                        modifier = Modifier.size(52.dp).then(imageSharedMod),
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
                        val titleSharedMod = if (sharedScope != null && animatedScope != null) {
                            with(sharedScope) {
                                Modifier.sharedBounds(
                                    sharedContentState = rememberSharedContentState("tidbit-title-${tidbit.id}"),
                                    animatedVisibilityScope = animatedScope
                                ).skipToLookaheadSize()
                            }
                        } else {
                            Modifier
                        }
                        Text(
                            text = tidbit.title,
                            modifier = titleSharedMod,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeTidbitSectionShimmer(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ShimmerBox(size = 52.dp, shape = RoundedCornerShape(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ShimmerBox(width = 48.dp, height = 10.dp)
                ShimmerBox(modifier = Modifier.fillMaxWidth(), height = 14.dp)
                ShimmerBox(width = 120.dp, height = 14.dp)
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun HomeTidbitSectionShimmerPreview() {
    GPTInvestorTheme {
        HomeTidbitSectionShimmer(modifier = Modifier.padding(16.dp))
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
