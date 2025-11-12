package com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@OptIn(UnstableApi::class)
@Composable
fun GptInvestorVideo(
    modifier: Modifier = Modifier,
    youtubeVideoId: String? = null,
    videoUrl: String? = null,
    autoplay: Boolean = false,
    showControls: Boolean = true,
    loop: Boolean = false,
    aspectRatioMode: Int = AspectRatioFrameLayout.RESIZE_MODE_FILL,
    onVideoStarted: (() -> Unit)? = null
) {
    val context = LocalContext.current

    if (youtubeVideoId != null) {
        AndroidView(
            factory = {
                YouTubePlayerView(context).apply {
                    enableAutomaticInitialization = false
                    val iFramePlayerOptionsBuilder = IFramePlayerOptions.Builder(context)
                        .controls(if (showControls) 1 else 0)

                    val iFramePlayerOptions = iFramePlayerOptionsBuilder.build()

                    initialize(
                        object : AbstractYouTubePlayerListener() {
                            override fun onReady(youTubePlayer: YouTubePlayer) {
                                if (autoplay) {
                                    youTubePlayer.loadVideo(videoId = youtubeVideoId, 0f)
                                } else {
                                    youTubePlayer.cueVideo(videoId = youtubeVideoId, 0f)
                                }
                            }

                            override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
                                if (state == PlayerConstants.PlayerState.PLAYING) {
                                    onVideoStarted?.invoke()
                                }
                            }
                        },
                        iFramePlayerOptions
                    )
                }
            },
            modifier = modifier
        )
    } else {
        // --- ExoPlayer Implementation ---
        videoUrl?.let { url ->
            val exoPlayer = remember(url) {
                ExoPlayer.Builder(context).build().apply {
                    setMediaItem(MediaItem.fromUri(url))
                    playWhenReady = autoplay
                    repeatMode = if (loop) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF

                    // Add listener for playback state changes
                    addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            if (playbackState == Player.STATE_READY && playWhenReady) {
                                onVideoStarted?.invoke()
                            }
                        }

                        override fun onIsPlayingChanged(isPlaying: Boolean) {
                            if (isPlaying) {
                                onVideoStarted?.invoke()
                            }
                        }
                    })

                    prepare()
                }
            }

            DisposableEffect(exoPlayer) {
                onDispose {
                    exoPlayer.release()
                }
            }

            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = showControls
                        resizeMode = aspectRatioMode
                    }
                },
                modifier = modifier
            )
        }
    }
}
