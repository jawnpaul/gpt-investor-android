package com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.CustomRichText
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.model.TidbitPresentation
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.viewmodel.TidbitAction
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.viewmodel.TidbitDetailState
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.viewmodel.TidbitEvent
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import com.thejawnpaul.gptinvestor.theme.bodyChatBody

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TidbitDetailScreen(
    modifier: Modifier = Modifier,
    tidbitId: String,
    state: TidbitDetailState,
    onEvent: (TidbitEvent) -> Unit,
    onAction: (TidbitAction) -> Unit // This onAction is for TidbitDetailScreen, might be used by other presentation types
) {
    LaunchedEffect(tidbitId) {
        onEvent(TidbitEvent.GetTidbit(tidbitId))
    }
    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
    state.presentation?.let { presentation ->
        when (presentation) {
            is TidbitPresentation.ArticlePresentation -> {
                TidbitArticleDetail(
                    modifier = modifier,
                    presentation = presentation,
                    onEvent = onEvent
                )
            }

            is TidbitPresentation.AudioPresentation -> {
                TidbitAudioDetail(
                    modifier = modifier,
                    presentation = presentation,
                    onEvent = onEvent
                )
            }

            is TidbitPresentation.VideoPresentation -> {
                TidbitVideoDetail(
                    modifier = modifier,
                    onEvent = onEvent,
                    presentation = presentation
                )
            }
        }
    }
}

@Composable
private fun DetailTopAppBar(modifier: Modifier = Modifier, onBackClicked: () -> Unit) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClicked) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = stringResource(id = R.string.back)
            )
        }
    }
}

@Composable
private fun AuthorCategoryInfo(modifier: Modifier = Modifier, author: String, category: String) {
    val gptInvestorColors = LocalGPTInvestorColors.current
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = author,
            style = MaterialTheme.typography.bodyChatBody
        )

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = gptInvestorColors.accentColors.allAccent20,
            contentColor = gptInvestorColors.accentColors.allAccent
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                text = category,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.W400,
                    lineHeight = 16.sp,
                    letterSpacing = 0.5.sp
                )
            )
        }
    }
}

@Composable
private fun TidbitActionRow(
    modifier: Modifier = Modifier,
    isLiked: Boolean,
    isBookmarked: Boolean,
    onLikeClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onShareClick: () -> Unit,
    onSourceClick: () -> Unit
) {
    val gptInvestorColors = LocalGPTInvestorColors.current
    val likeIcon = if (isLiked) R.drawable.ic_like_filled else R.drawable.ic_like
    val bookmarkIcon = if (isBookmarked) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(modifier = Modifier.size(32.dp), onClick = onLikeClick) {
                Icon(
                    painter = painterResource(id = likeIcon),
                    contentDescription = stringResource(R.string.like)
                )
            }
            IconButton(modifier = Modifier.size(32.dp), onClick = onBookmarkClick) {
                Icon(
                    painter = painterResource(id = bookmarkIcon),
                    contentDescription = stringResource(R.string.bookmark)
                )
            }
            IconButton(modifier = Modifier.size(32.dp), onClick = onShareClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_top_pick_send),
                    contentDescription = stringResource(R.string.share)
                )
            }
        }
        Surface(
            shape = RoundedCornerShape(corner = CornerSize(20.dp)),
            border = BorderStroke(
                width = 2.dp,
                color = gptInvestorColors.utilColors.borderBright10
            ),
            onClick = onSourceClick
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.source))
                Icon(
                    painter = painterResource(id = R.drawable.ic_global),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun TidbitArticleDetail(modifier: Modifier = Modifier, presentation: TidbitPresentation.ArticlePresentation, onEvent: (TidbitEvent) -> Unit) {
    val isLiked = remember { mutableStateOf(false) } // State managed here
    val isBookmarked = remember { mutableStateOf(false) } // State managed here

    Scaffold(
        modifier = modifier,
        topBar = {
            DetailTopAppBar(onBackClicked = { onEvent(TidbitEvent.GoBack) })
        },
        bottomBar = {
            TidbitActionRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                isLiked = isLiked.value,
                isBookmarked = isBookmarked.value,
                onLikeClick = {
                    // onEvent(TidbitEvent.Like(!isLiked.value)) // Example event
                    isLiked.value = !isLiked.value
                },
                onBookmarkClick = {
                    // onEvent(TidbitEvent.Bookmark(!isBookmarked.value)) // Example event
                    isBookmarked.value = !isBookmarked.value
                },
                onShareClick = { onEvent(TidbitEvent.OnClickShare) },
                onSourceClick = { onEvent(TidbitEvent.OnClickSource) }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Spacer(modifier = Modifier)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(193.dp),
                    shape = RoundedCornerShape(corner = CornerSize(20.dp))
                ) {
                    AsyncImage(
                        modifier = Modifier.fillMaxSize(),
                        model = presentation.mediaUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }

                AuthorCategoryInfo(
                    modifier = Modifier.fillMaxWidth(),
                    author = presentation.originalAuthor,
                    category = presentation.category
                )

                Text(text = presentation.title, style = MaterialTheme.typography.titleLarge)
                CustomRichText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = presentation.content
                )
                Spacer(modifier = Modifier)
            }
        }
    }
}

@Composable
private fun TidbitVideoDetail(modifier: Modifier = Modifier, presentation: TidbitPresentation.VideoPresentation, onEvent: (TidbitEvent) -> Unit) {
    val isLiked = remember { mutableStateOf(false) } // State managed here
    val isBookmarked = remember { mutableStateOf(false) } // State managed here

    Scaffold(
        modifier = modifier,
        topBar = {
            DetailTopAppBar(onBackClicked = { onEvent(TidbitEvent.GoBack) })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Apply scaffold padding
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp) // Consistent spacing for children
        ) {
            Spacer(modifier = Modifier.height(16.dp)) // Initial spacer from top bar

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 200.dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(corner = CornerSize(20.dp))
            ) {
                GptInvestorVideo( // Assuming this is a custom composable
                    modifier = Modifier.fillMaxSize(),
                    youtubeVideoId = presentation.videoId,
                    videoUrl = presentation.mediaUrl,
                    autoplay = true,
                    showControls = false
                )
            }

            AuthorCategoryInfo(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp), // Consistent padding
                author = presentation.originalAuthor,
                category = presentation.category
            )

            Text(
                modifier = Modifier.padding(horizontal = 16.dp), // Consistent padding
                text = presentation.title, // Assuming VideoPresentation also has a title
                style = MaterialTheme.typography.titleLarge // Video title, if exists
            )

            CustomRichText(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = presentation.content
            )

            TidbitActionRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), // Consistent padding for the action row
                isLiked = isLiked.value,
                isBookmarked = isBookmarked.value,
                onLikeClick = { isLiked.value = !isLiked.value },
                onBookmarkClick = { isBookmarked.value = !isBookmarked.value },
                onShareClick = { onEvent(TidbitEvent.OnClickShare) },
                onSourceClick = { onEvent(TidbitEvent.OnClickSource) }
            )
            Spacer(modifier = Modifier.height(16.dp)) // Final spacer at the bottom
        }
    }
}

@Composable
private fun TidbitAudioDetail(modifier: Modifier = Modifier, presentation: TidbitPresentation.AudioPresentation, onEvent: (TidbitEvent) -> Unit) {
    val isLiked = remember { mutableStateOf(false) } // State managed here
    val isBookmarked = remember { mutableStateOf(false) } // State managed here

    Scaffold(
        modifier = modifier,
        topBar = {
            DetailTopAppBar(onBackClicked = { onEvent(TidbitEvent.GoBack) })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Apply scaffold padding
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp) // Consistent spacing for children
        ) {
            Spacer(modifier = Modifier.height(16.dp)) // Initial spacer from top bar

            GptInvestorAudioCompact(
                modifier = Modifier.fillMaxSize(),
                artworkUrl = presentation.previewUrl,
                audioUrl = presentation.mediaUrl,
                autoplay = true
            )

            AuthorCategoryInfo(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp), // Consistent padding
                author = presentation.originalAuthor,
                category = presentation.category
            )

            Text(
                modifier = Modifier.padding(horizontal = 16.dp), // Consistent padding
                text = presentation.title, // Assuming AudioPresentation also has a title
                style = MaterialTheme.typography.titleLarge // Audio title, if exists
            )

            CustomRichText(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = presentation.content
            )

            TidbitActionRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), // Consistent padding for the action row
                isLiked = isLiked.value,
                isBookmarked = isBookmarked.value,
                onLikeClick = { isLiked.value = !isLiked.value },
                onBookmarkClick = { isBookmarked.value = !isBookmarked.value },
                onShareClick = { onEvent(TidbitEvent.OnClickShare) },
                onSourceClick = { onEvent(TidbitEvent.OnClickSource) }
            )
            Spacer(modifier = Modifier.height(16.dp)) // Final spacer at the bottom
        }
    }
}

@Preview
@Composable
fun TidbitDetailScreenPreview() {
    GPTInvestorTheme {
        TidbitDetailScreen(
            tidbitId = "1",
            onEvent = {},
            onAction = {},
            state = TidbitDetailState(
                id = "1",
                presentation = TidbitPresentation.AudioPresentation(
                    id = "1",
                    name = "GPT Investor",
                    previewUrl = "https://example.com/image.jpg", // Example preview URL
                    mediaUrl = "https://example.com/audio.mp3", // Example audio URL
                    title = "Title",
                    content = "Content",
                    originalAuthor = "Original Author",
                    category = "Category",
                    sourceUrl = "Source URL"
                )
            )
        )
    }
}
