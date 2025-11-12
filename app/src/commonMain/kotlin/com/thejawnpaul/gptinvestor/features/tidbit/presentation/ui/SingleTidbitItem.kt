package com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.model.TidbitPresentation
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import gptinvestor.app.generated.resources.Res
import gptinvestor.app.generated.resources.ic_bookmark
import gptinvestor.app.generated.resources.ic_bookmark_filled
import gptinvestor.app.generated.resources.ic_like
import gptinvestor.app.generated.resources.ic_like_filled
import gptinvestor.app.generated.resources.ic_tidbit_item_article
import gptinvestor.app.generated.resources.ic_tidbit_item_audio
import gptinvestor.app.generated.resources.ic_tidbit_item_video
import gptinvestor.app.generated.resources.ic_top_pick_send
import org.jetbrains.compose.resources.painterResource

@Composable
fun SingleTidbitItem(
    modifier: Modifier = Modifier,
    tidbit: TidbitPresentation,
    onItemClick: (String) -> Unit,
    onLikeClick: (String, Boolean) -> Unit = { _, _ -> },
    onSaveClick: (String, Boolean) -> Unit = { _, _ -> },
    onShareClick: (String) -> Unit = {}
) {
    val gptInvestorColors = LocalGPTInvestorColors.current
    val isLiked = remember { mutableStateOf(tidbit.isLiked) }
    val isBookmarked = remember { mutableStateOf(tidbit.isBookmarked) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClick(tidbit.id) },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = tidbit.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = tidbit.summary,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = tidbit.previewUrl.ifEmpty { "" },
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Play icon for video/audio
                when (tidbit) {
                    is TidbitPresentation.VideoPresentation, is TidbitPresentation.AudioPresentation -> {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(48.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    else -> {}
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val likeIcon = if (isLiked.value) Res.drawable.ic_like_filled else Res.drawable.ic_like
                val bookmarkIcon =
                    if (isBookmarked.value) Res.drawable.ic_bookmark_filled else Res.drawable.ic_bookmark
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            onLikeClick(tidbit.id, !isLiked.value)
                            isLiked.value = !isLiked.value
                        },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            painter = painterResource(likeIcon),
                            contentDescription = "Like"
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    IconButton(
                        onClick = {
                            onSaveClick(tidbit.id, !isBookmarked.value)
                            isBookmarked.value = !isBookmarked.value
                        },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            painter = painterResource(bookmarkIcon),
                            contentDescription = "Save"
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    IconButton(
                        onClick = { onShareClick(tidbit.id) },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_top_pick_send),
                            contentDescription = "Share"
                        )
                    }
                }

                // Media type tag
                val mediaTypeIcon: Painter?
                val mediaTypeText: String?
                when (tidbit) {
                    is TidbitPresentation.ArticlePresentation -> {
                        mediaTypeIcon =
                            painterResource(Res.drawable.ic_tidbit_item_article)
                        mediaTypeText = "Article"
                    }

                    is TidbitPresentation.VideoPresentation -> {
                        mediaTypeIcon =
                            painterResource(Res.drawable.ic_tidbit_item_video)
                        mediaTypeText = "Video"
                    }

                    is TidbitPresentation.AudioPresentation -> {
                        mediaTypeIcon =
                            painterResource(Res.drawable.ic_tidbit_item_audio)
                        mediaTypeText = "Audio"
                    }
                }
                Surface(
                    modifier = Modifier,
                    shape = RoundedCornerShape(20.dp),
                    color = gptInvestorColors.accentColors.allAccent20
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Image(
                            painter = mediaTypeIcon,
                            contentDescription = mediaTypeText,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = mediaTypeText,
                            style = MaterialTheme.typography.labelSmall,
                            color = gptInvestorColors.accentColors.allAccent
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = tidbit.originalAuthor,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = gptInvestorColors.accentColors.allAccent20,
                        contentColor = gptInvestorColors.utilColors.borderBright10
                    ) {
                        Text(
                            text = tidbit.category,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color = gptInvestorColors.accentColors.allAccent
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    /*Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = gptInvestorColors.greenColors.allGreen10.copy(alpha = 0.5f),
                        contentColor = gptInvestorColors.greenColors.allGreen
                    ) {
                        Text(
                            text = "+2",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }*/
                }
            }
        }
    }
}

class TidbitPreviewParameterProvider : PreviewParameterProvider<TidbitPresentation> {
    override val values = sequenceOf(
        TidbitPresentation.ArticlePresentation(
            id = "article1",
            name = "Exploring Jetpack Compose",
            previewUrl = "https://images.unsplash.com/photo-1607025898864-1853606f7347?ixid=MnwxMjA3fDB8MHxzZWFyY2h8MXx8Y29tcG9zZXxlbnwwfHwwfHw%3D&ixlib=rb-1.2.1&w=1000&q=80",
            mediaUrl = "",
            title = "Deep Dive into Declarative UIs with Jetpack Compose",
            content = "Jetpack Compose is Android’s modern toolkit for building native UI. It simplifies and accelerates UI development on Android. Learn how to get started and explore its powerful features.",
            originalAuthor = "Android Developers",
            category = "Android Development",
            sourceUrl = "https://developer.android.com/jetpack/compose"
        ),
        TidbitPresentation.VideoPresentation(
            id = "video1",
            name = "Compose for Beginners",
            previewUrl = "https://images.unsplash.com/photo-1542831371-29b0f74f9713?ixid=MnwxMjA3fDB8MHxzZWFyY2h8NHx8Y29kZSUyMHZpZGVvfGVufDB8fDB8fA%3D%3D&ixlib=rb-1.2.1&w=1000&q=80",
            mediaUrl = "", // Usually different from previewUrl for videos
            title = "Getting Started with Jetpack Compose: A Video Tutorial",
            content = "This video walks you through the basics of setting up your first Jetpack Compose project, creating simple UI elements, and understanding the core concepts of declarative programming.",
            originalAuthor = "Compose Community",
            category = "Tutorials",
            sourceUrl = "https://youtube.com/jetpackcompose"
        ),
        TidbitPresentation.AudioPresentation(
            id = "audio1",
            name = "Compose Podcast",
            previewUrl = "https://images.unsplash.com/photo-1587160983999-a57e487ef00c?ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8YXVkaW8lMjBwb2RjYXN0fGVufDB8fDB8fA%3D%3D&ixlib=rb-1.2.1&w=1000&q=80",
            mediaUrl = "", // Actual audio file URL
            title = "The Future of UI: A Podcast on Jetpack Compose",
            content = "Listen to experts discuss the impact of Jetpack Compose on Android development, its adoption challenges, and best practices for building scalable applications.",
            originalAuthor = "Android Dev Talks",
            category = "Podcast",
            sourceUrl = "https://anchor.fm/androiddevtalks"
        )
    )
}

@Preview(showBackground = true, name = "Single Tidbit Item Preview")
@Composable
fun SingleTidbitItemPreview(@PreviewParameter(TidbitPreviewParameterProvider::class) tidbit: TidbitPresentation) {
    GPTInvestorTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            SingleTidbitItem(
                modifier = Modifier.padding(16.dp),
                tidbit = tidbit,
                onItemClick = {},
                onLikeClick = { _, _ -> },
                onSaveClick = { _, _ -> },
                onShareClick = {}
            )
        }
    }
}
