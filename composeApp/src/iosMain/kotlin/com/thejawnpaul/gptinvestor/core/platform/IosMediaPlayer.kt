package com.thejawnpaul.gptinvestor.core.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import androidx.compose.ui.viewinterop.UIKitViewController
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Singleton
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerItemDidPlayToEndTimeNotification
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.seekToTime
import platform.AVKit.AVPlayerViewController
import platform.CoreMedia.CMTimeMake
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL

@Singleton(binds = [MediaPlayer::class])
class IosMediaPlayer(@Provided private val youtubeProvider: YoutubePlayerProvider) : MediaPlayer {

    @OptIn(ExperimentalForeignApi::class)
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
        if (isYoutube && youtubeVideoId != null) {
            val playerView = remember(youtubeVideoId, autoplay, showControls) {
                youtubeProvider.createPlayerView(youtubeVideoId, autoplay, showControls)
            }
            UIKitView(
                factory = { playerView },
                modifier = modifier
            )
        } else {
            val nsUrl = remember(url) { NSURL.URLWithString(url) } ?: return
            val playerItem = remember(nsUrl) { AVPlayerItem(uRL = nsUrl) }
            val player = remember(playerItem) { AVPlayer(playerItem = playerItem) }

            DisposableEffect(player, loop) {
                if (autoplay) player.play()

                val loopObserver = if (loop) {
                    NSNotificationCenter.defaultCenter.addObserverForName(
                        name = AVPlayerItemDidPlayToEndTimeNotification,
                        `object` = playerItem,
                        queue = NSOperationQueue.mainQueue,
                        usingBlock = {
                            player.seekToTime(CMTimeMake(value = 0, timescale = 1))
                            player.play()
                        }
                    )
                } else {
                    null
                }

                onDispose {
                    player.pause()
                    loopObserver?.let { NSNotificationCenter.defaultCenter.removeObserver(it) }
                }
            }

            UIKitViewController(
                factory = {
                    AVPlayerViewController().apply {
                        this.player = player
                        showsPlaybackControls = showControls
                    }
                },
                modifier = modifier
            )
        }
    }

    @Composable
    override fun Audio(audioUrl: String, modifier: Modifier) {
        val nsUrl = remember(audioUrl) { NSURL.URLWithString(audioUrl) } ?: return
        val player = remember(nsUrl) { AVPlayer(uRL = nsUrl) }

        DisposableEffect(player) {
            onDispose { player.pause() }
        }

        UIKitViewController(
            factory = {
                AVPlayerViewController().apply {
                    this.player = player
                    showsPlaybackControls = true
                }
            },
            modifier = modifier
        )
    }
}
