package com.thejawnpaul.gptinvestor.core.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui.AndroidGptInvestorAudio
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui.AndroidGptInvestorVideo
import org.koin.core.annotation.Singleton

@Singleton(binds = [MediaPlayer::class])
class AndroidMediaPlayer : MediaPlayer {
    @Composable
    override fun Video(
        url: String,
        autoplay: Boolean,
        loop: Boolean,
        isYoutube: Boolean,
        showControls: Boolean,
        youtubeVideoId: String?,
        modifier: Modifier
    ) {
        AndroidGptInvestorVideo(
            videoUrl = url,
            autoplay = autoplay,
            loop = loop,
            youtubeVideoId = youtubeVideoId,
            modifier = modifier
        )
    }

    @Composable
    override fun Audio(audioUrl: String, modifier: Modifier) {
        AndroidGptInvestorAudio(audioUrl = audioUrl, modifier = modifier)
    }
}
