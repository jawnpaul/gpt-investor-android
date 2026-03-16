package com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.thejawnpaul.gptinvestor.core.platform.MediaPlayer
import org.koin.compose.koinInject

@Composable
fun GptInvestorVideo(
    url: String,
    modifier: Modifier = Modifier,
    autoplay: Boolean = true,
    loop: Boolean = false,
    isYoutube: Boolean = false,
    showControls: Boolean = false,
    youtubeVideoId: String? = null
) {
    val mediaPlayer: MediaPlayer = koinInject()
    mediaPlayer.Video(
        url = url,
        autoplay = autoplay,
        loop = loop,
        isYoutube = isYoutube,
        showControls = showControls,
        youtubeVideoId = youtubeVideoId,
        modifier = modifier
    )
}
