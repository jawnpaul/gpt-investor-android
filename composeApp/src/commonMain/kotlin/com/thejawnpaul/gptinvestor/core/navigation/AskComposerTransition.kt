@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope.ResizeMode.Companion.RemeasureToBounds
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled

@Composable
fun AnimatedVisibilityScope.askComposerTransition(): Modifier {
    val sharedScope = LocalSharedTransitionScope.current
    if (sharedScope == null || isReducedMotionEnabled()) return Modifier
    return with(sharedScope) {
        Modifier.sharedBounds(
            sharedContentState = rememberSharedContentState(ASK_COMPOSER_KEY),
            animatedVisibilityScope = this@askComposerTransition,
            boundsTransform = askComposerBounds,
            resizeMode = RemeasureToBounds
        )
    }
}

private val askComposerBounds = BoundsTransform { _, _ -> GiMotion.smooth() }

private const val ASK_COMPOSER_KEY = "ask-composer"
