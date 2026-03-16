package com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.thejawnpaul.gptinvestor.core.platform.MediaPlayer
import org.koin.compose.koinInject

@Composable
fun GptInvestorAudio(audioUrl: String, modifier: Modifier = Modifier) {
    val mediaPlayer: MediaPlayer = koinInject()
    mediaPlayer.Audio(audioUrl = audioUrl, modifier = modifier)
}
