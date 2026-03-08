package com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun GptInvestorAudio(
    modifier: Modifier = Modifier,
    audioUrl: String,
    artworkUrl: String? = null,
    autoplay: Boolean = false,
    showControls: Boolean = true,
    loop: Boolean = false,
    onAudioStarted: (() -> Unit)? = null,
    onAudioEnded: (() -> Unit)? = null,
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
                                onAudioStarted?.invoke()
                            }
                        }

                        Player.STATE_ENDED -> {
                            onAudioEnded?.invoke()
                        }
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                        onAudioStarted?.invoke()
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
                useArtwork = true
                defaultArtwork = ContextCompat.getDrawable(ctx, android.R.drawable.ic_media_play)
                controllerShowTimeoutMs =
                    if (showControls) 0 else -1 // Always show controls or never show
            }
        },
        modifier = modifier
    )
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun GptInvestorAudioCompact(
    modifier: Modifier = Modifier,
    audioUrl: String,
    artworkUrl: String? = null,
    title: String? = null,
    autoplay: Boolean = false,
    loop: Boolean = false,
    onAudioStarted: (() -> Unit)? = null,
    onAudioEnded: (() -> Unit)? = null,
    onError: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var duration by remember { mutableLongStateOf(0L) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    val gptInvestorColors = LocalGPTInvestorColors.current

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
                                onAudioStarted?.invoke()
                            }
                        }

                        Player.STATE_ENDED -> {
                            onAudioEnded?.invoke()
                        }
                    }
                }

                override fun onIsPlayingChanged(playing: Boolean) {
                    isPlaying = playing
                    if (playing) {
                        onAudioStarted?.invoke()
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

    Column(
        modifier = modifier.padding(16.dp)
    ) {
        // Artwork display
        artworkUrl?.let { url ->
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(url)
                    .crossfade(true)
                    .build(),
                contentDescription = "Audio artwork",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(193.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                error = painterResource(android.R.drawable.ic_media_play),
                placeholder = painterResource(android.R.drawable.ic_media_play)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Title
        title?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Play/Pause button
            IconButton(
                onClick = {
                    if (isPlaying) {
                        exoPlayer.pause()
                    } else {
                        exoPlayer.play()
                    }
                }
            ) {
                val icon =
                    if (isPlaying) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }

            Text(
                modifier = Modifier.padding(end = 8.dp),
                text = formatTime(currentPosition),
                style = MaterialTheme.typography.bodySmall
            )

            val animatedProgress by
                animateFloatAsState(
                    targetValue = currentPosition.toFloat() / duration.toFloat(),
                    animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
                )

            // Progress bar
            if (duration > 0) {
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp),
                    trackColor = Color.Gray,
                    strokeCap = StrokeCap.Round,
                    color = gptInvestorColors.accentColors.allAccent20
                )
            }

            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = if (duration > 0) formatTime(duration) else "--:--",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

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
