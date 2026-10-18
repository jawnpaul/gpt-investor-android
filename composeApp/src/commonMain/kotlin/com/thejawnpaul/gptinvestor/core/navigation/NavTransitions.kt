package com.thejawnpaul.gptinvestor.core.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavBackStackEntry
import com.thejawnpaul.gptinvestor.core.platform.isIos
import com.thejawnpaul.gptinvestor.theme.GiMotion

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

private const val SHARED_AXIS_OFFSET_FRACTION = 0.08f

private val authRoutes = setOf(
    Screen.DefaultAuthenticationScreen.route,
    Screen.SignUpScreen.route,
    Screen.LoginScreen.route
)

private fun AnimatedContentTransitionScope<NavBackStackEntry>.isWithinAuth() =
    initialState.destination.route in authRoutes && targetState.destination.route in authRoutes

private fun sharedAxisOffset(fullWidth: Int) = (fullWidth * SHARED_AXIS_OFFSET_FRACTION).toInt()

private fun AnimatedContentTransitionScope<NavBackStackEntry>.sharedAxisEnter(
    towards: AnimatedContentTransitionScope.SlideDirection
): EnterTransition = slideIntoContainer(towards, GiMotion.smooth(), ::sharedAxisOffset) +
    fadeIn(tween(GiMotion.BASE, delayMillis = GiMotion.INSTANT, easing = EaseOutQuart))

private fun AnimatedContentTransitionScope<NavBackStackEntry>.sharedAxisExit(
    towards: AnimatedContentTransitionScope.SlideDirection
): ExitTransition = slideOutOfContainer(towards, GiMotion.smooth(), ::sharedAxisOffset) +
    fadeOut(tween(GiMotion.INSTANT, easing = LinearEasing))

val navEnterTransition: NavEnterTransition = {
    when {
        !isIos && isWithinAuth() -> sharedAxisEnter(AnimatedContentTransitionScope.SlideDirection.Start)
        !isIos -> fadeIn(tween(DEFAULT_DURATION_MILLIS))
        isTabSwitch() -> fadeIn(tween(IOS_TAB_FADE_DURATION_MILLIS))
        else -> slideInHorizontally(iosSlideSpec()) { it }
    }
}

val navExitTransition: NavExitTransition = {
    when {
        !isIos && isWithinAuth() -> sharedAxisExit(AnimatedContentTransitionScope.SlideDirection.Start)
        !isIos -> fadeOut(tween(DEFAULT_DURATION_MILLIS))
        isTabSwitch() -> fadeOut(tween(IOS_TAB_FADE_DURATION_MILLIS))
        else -> slideOutHorizontally(iosSlideSpec()) { -it / IOS_PARALLAX_DIVISOR }
    }
}

val navPopEnterTransition: NavEnterTransition = {
    when {
        !isIos && isWithinAuth() -> sharedAxisEnter(AnimatedContentTransitionScope.SlideDirection.End)
        !isIos -> fadeIn(tween(DEFAULT_DURATION_MILLIS))
        isTabSwitch() -> fadeIn(tween(IOS_TAB_FADE_DURATION_MILLIS))
        else -> slideInHorizontally(iosSlideSpec()) { -it / IOS_PARALLAX_DIVISOR }
    }
}

val navPopExitTransition: NavExitTransition = {
    when {
        !isIos && isWithinAuth() -> sharedAxisExit(AnimatedContentTransitionScope.SlideDirection.End)
        !isIos -> fadeOut(tween(DEFAULT_DURATION_MILLIS))
        isTabSwitch() -> fadeOut(tween(IOS_TAB_FADE_DURATION_MILLIS))
        else -> slideOutHorizontally(iosSlideSpec()) { it }
    }
}
