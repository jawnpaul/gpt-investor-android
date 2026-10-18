@file:JvmName("AndroidGptInvestorAudioKt")

package com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.audio_pause
import com.thejawnpaul.gptinvestor.audio_play
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiIconButton
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import timber.log.Timber

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun AndroidGptInvestorAudio(
    audioUrl: String,
    modifier: Modifier = Modifier,
    artworkUrl: String? = null,
    autoplay: Boolean = false,
    showControls: Boolean = true,
    loop: Boolean = false,
    onAudioStart: (() -> Unit)? = null,
    onAudioEnd: (() -> Unit)? = null,
    onError: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    var artworkBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Load artwork from URL
    LaunchedEffect(artworkUrl) {
        artworkUrl?.let { url ->
            try {
                withContext(Dispatchers.IO) {
                    val connection = URL(url).openConnection()
                    connection.doInput = true
                    connection.connect()
                    val inputStream = connection.getInputStream()
                    artworkBitmap = BitmapFactory.decodeStream(inputStream)
                    Timber.e("Artwork loaded successfully")
                }
            } catch (e: Exception) {
                // Handle image loading error silently, fall back to default
                Timber.e(e.stackTraceToString())
                artworkBitmap = null
            }
        }
    }

    val exoPlayer = remember(audioUrl) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(audioUrl))
            playWhenReady = autoplay
            repeatMode = if (loop) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF

            // Add listener for playback state changes
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_READY -> {
                            if (playWhenReady) {
                                onAudioStart?.invoke()
                            }
                        }

                        Player.STATE_ENDED -> {
                            onAudioEnd?.invoke()
                        }
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                        onAudioStart?.invoke()
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    onError?.invoke(error.message ?: "Unknown playback error")
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
                useArtwork = false
                controllerShowTimeoutMs =
                    if (showControls) 0 else -1 // Always show controls or never show
            }
        },
        modifier = modifier
    )
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun AndroidGptInvestorAudioCompact(
    audioUrl: String,
    modifier: Modifier = Modifier,
    artworkUrl: String? = null,
    title: String? = null,
    autoplay: Boolean = false,
    loop: Boolean = false,
    onAudioStart: (() -> Unit)? = null,
    onAudioEnd: (() -> Unit)? = null,
    onError: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var duration by remember { mutableLongStateOf(0L) }
    var currentPosition by remember { mutableLongStateOf(0L) }

    val exoPlayer = remember(audioUrl) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(audioUrl))
            playWhenReady = autoplay
            repeatMode = if (loop) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF

            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_READY -> {
                            duration = this@apply.duration
                            if (playWhenReady) {
                                onAudioStart?.invoke()
                            }
                        }

                        Player.STATE_ENDED -> {
                            onAudioEnd?.invoke()
                        }
                    }
                }

                override fun onIsPlayingChanged(playing: Boolean) {
                    isPlaying = playing
                    if (playing) {
                        onAudioStart?.invoke()
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    onError?.invoke(error.message ?: "Unknown playback error")
                }
            })

            prepare()
        }
    }

    // Update current position periodically
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            currentPosition = exoPlayer.currentPosition
            delay(100)
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    val colors = GiTheme.colors
    Column(
        modifier = modifier.padding(GiSpace.cardPad),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x2)
    ) {
        // Artwork display
        artworkUrl?.let { url ->
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(url)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ARTWORK_HEIGHT)
                    .clip(GiRadius.md)
                    .background(colors.violetSoft),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(GiSpace.x2))
        }

        title?.let {
            GiText(text = it, style = GiTextStyle.SectionTitle)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x2),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GiIconButton(
                icon = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = stringResource(
                    if (isPlaying) Res.string.audio_pause else Res.string.audio_play
                ),
                onClick = {
                    if (isPlaying) {
                        exoPlayer.pause()
                    } else {
                        exoPlayer.play()
                    }
                }
            )

            GiText(text = formatTime(currentPosition), style = GiTextStyle.Meta)

            val animatedProgress by animateFloatAsState(
                targetValue = if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f,
                animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
            )

            if (duration > 0) {
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .weight(1f)
                        .height(GiSpace.x1)
                        .clip(GiRadius.full),
                    color = colors.violet,
                    trackColor = colors.track,
                    strokeCap = StrokeCap.Round,
                    gapSize = GiSpace.x0_5,
                    drawStopIndicator = {}
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            GiText(text = if (duration > 0) formatTime(duration) else "--:--", style = GiTextStyle.Meta)
        }
    }
}

private val ARTWORK_HEIGHT = 193.dp

private fun formatTime(timeMs: Long): String {
    val seconds = (timeMs / 1000) % 60
    val minutes = (timeMs / 1000 / 60) % 60
    val hours = timeMs / 1000 / 60 / 60

    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%d:%02d", minutes, seconds)
    }
}
