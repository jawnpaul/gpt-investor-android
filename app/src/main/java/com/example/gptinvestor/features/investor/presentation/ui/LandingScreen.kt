package com.example.gptinvestor.features.investor.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.gptinvestor.R
import kotlinx.coroutines.delay

private const val SplashWaitTime: Long = 2000

@Composable
fun LandingScreen(onTimeout: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val currentOnTimeout by rememberUpdatedState(onTimeout)
        LaunchedEffect(Unit) {
            delay(SplashWaitTime)
            currentOnTimeout()
        }

        Image(painterResource(id = R.drawable.baseline_diamond_24), contentDescription = null)
    }
}