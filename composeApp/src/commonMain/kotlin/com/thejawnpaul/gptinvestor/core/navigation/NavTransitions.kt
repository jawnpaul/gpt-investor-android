package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavBackStackEntry
import com.thejawnpaul.gptinvestor.core.platform.isIos

typealias NavEnterTransition = AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition
typealias NavExitTransition = AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition

private const val DEFAULT_DURATION_MILLIS = 700
private const val IOS_SLIDE_DURATION_MILLIS = 350
private const val IOS_TAB_FADE_DURATION_MILLIS = 200
private const val IOS_PARALLAX_DIVISOR = 3

private val iosSlideEasing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1f)

private fun AnimatedContentTransitionScope<NavBackStackEntry>.isTabSwitch() =
    isBottomTabRoute(initialState.destination.route) && isBottomTabRoute(targetState.destination.route)

private fun <T> iosSlideSpec() = tween<T>(IOS_SLIDE_DURATION_MILLIS, easing = iosSlideEasing)

val navEnterTransition: NavEnterTransition = {
    when {
        !isIos -> fadeIn(tween(DEFAULT_DURATION_MILLIS))
        isTabSwitch() -> fadeIn(tween(IOS_TAB_FADE_DURATION_MILLIS))
        else -> slideInHorizontally(iosSlideSpec()) { it }
    }
}

val navExitTransition: NavExitTransition = {
    when {
        !isIos -> fadeOut(tween(DEFAULT_DURATION_MILLIS))
        isTabSwitch() -> fadeOut(tween(IOS_TAB_FADE_DURATION_MILLIS))
        else -> slideOutHorizontally(iosSlideSpec()) { -it / IOS_PARALLAX_DIVISOR }
    }
}

val navPopEnterTransition: NavEnterTransition = {
    when {
        !isIos -> fadeIn(tween(DEFAULT_DURATION_MILLIS))
        isTabSwitch() -> fadeIn(tween(IOS_TAB_FADE_DURATION_MILLIS))
        else -> slideInHorizontally(iosSlideSpec()) { -it / IOS_PARALLAX_DIVISOR }
    }
}

val navPopExitTransition: NavExitTransition = {
    when {
        !isIos -> fadeOut(tween(DEFAULT_DURATION_MILLIS))
        isTabSwitch() -> fadeOut(tween(IOS_TAB_FADE_DURATION_MILLIS))
        else -> slideOutHorizontally(iosSlideSpec()) { it }
    }
}
