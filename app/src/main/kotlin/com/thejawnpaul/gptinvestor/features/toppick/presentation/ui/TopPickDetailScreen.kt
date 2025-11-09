package com.thejawnpaul.gptinvestor.features.toppick.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.features.authentication.presentation.NewAuthenticationScreen
import com.thejawnpaul.gptinvestor.features.company.presentation.state.CompanyHeaderPresentation
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.CompanyDetailHeader
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.CompanyDetailTab
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.ExpandableText
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.GptInvestorBottomSheet
import com.thejawnpaul.gptinvestor.features.toppick.presentation.TopPickAction
import com.thejawnpaul.gptinvestor.features.toppick.presentation.TopPickEvent
import com.thejawnpaul.gptinvestor.features.toppick.presentation.state.TopPickDetailView
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import com.thejawnpaul.gptinvestor.theme.linkMedium

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopPickDetailScreen(modifier: Modifier, topPickId: String, state: TopPickDetailView, onEvent: (TopPickEvent) -> Unit, onAction: (TopPickAction) -> Unit) {
    LaunchedEffect(topPickId) {
        onEvent(TopPickEvent.GetTopPick(topPickId))
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            state.companyPresentation?.let { company ->
                CompanyDetailHeader(
                    modifier = Modifier,
                    companyHeader = CompanyHeaderPresentation(
                        companyTicker = company.ticker,
                        companyName = company.name,
                        price = company.price,
                        percentageChange = company.change,
                        companyLogo = company.imageUrl
                    ),
                    onNavigateUp = { onAction(TopPickAction.OnGoBack) }
                )
            } ?: Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = { onAction(TopPickAction.OnGoBack) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = stringResource(id = R.string.back)
                    )
                }
                Text(text = stringResource(R.string.top_pick_details))
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isLiked = remember { mutableStateOf(false) }
                    val likeIcon = if (isLiked.value) {
                        R.drawable.ic_like_filled
                    } else {
                        R.drawable.ic_like
                    }

                    val bookmarkIcon = if (state.topPick?.isSaved == true) {
                        R.drawable.ic_bookmark_filled
                    } else {
                        R.drawable.ic_bookmark
                    }

                    // Like
                    IconButton(
                        modifier = Modifier.size(32.dp),
                        onClick = {
                            if (isLiked.value) {
                                onEvent(TopPickEvent.RemoveLikeTopPick)
                            } else {
                                onEvent(TopPickEvent.LikeTopPick)
                            }
                            isLiked.value = !isLiked.value
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = likeIcon),
                            contentDescription = null
                        )
                    }

                    // Bookmark
                    IconButton(
                        modifier = Modifier.size(32.dp),
                        onClick = {
                            if (state.topPick?.isSaved == true) {
                                onEvent(TopPickEvent.RemoveBookmarkTopPick)
                            } else {
                                onEvent(TopPickEvent.BookmarkTopPick)
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = bookmarkIcon),
                            contentDescription = stringResource(R.string.like_chosen)
                        )
                    }
                }

                // Share
                IconButton(
                    modifier = Modifier.size(32.dp),
                    onClick = {
                        onEvent(TopPickEvent.ShareTopPick)
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_top_pick_send),
                        contentDescription = null
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            ContentView(
                modifier = Modifier.fillMaxSize(),
                state = state,
                onEvent = onEvent
            )
        }

        if (state.showNewsSourcesBottomSheet) {
            GptInvestorBottomSheet(
                modifier = Modifier,
                onDismiss = {
                    onEvent(TopPickEvent.ClickNewsSources(show = false))
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.sources),
                        style = MaterialTheme.typography.labelMedium
                    )
                    state.companyPresentation?.news?.map { it.toPresentation() }
                        ?.let { news ->
                            news.forEachIndexed { index, item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(
                                        8.dp
                                    )
                                ) {
                                    AsyncImage(
                                        model = item.imageUrl,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop
                                    )

                                    Text(
                                        text = item.publisher,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }

                                if (index != news.lastIndex) {
                                    HorizontalDivider()
                                }
                            }
                        }
                }
            }
        }
    }
}

@Composable
private fun ContentView(modifier: Modifier, state: TopPickDetailView, onEvent: (TopPickEvent) -> Unit) {
    val gptInvestorColors = LocalGPTInvestorColors.current
    Box(
        modifier = modifier.clickable(interactionSource = null, indication = null, onClick = {
            onEvent(TopPickEvent.Authenticate(showDialog = false))
        })
    ) {
        if (state.showAuthenticateDialog) {
            NewAuthenticationScreen(
                modifier = Modifier.align(Alignment.Center),
                onAuthenticationComplete = { onEvent(TopPickEvent.AuthenticationResponse(it)) }
            )
            Column(modifier = Modifier.blur(radius = 8.dp)) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_top_pick_rationale),
                                contentDescription = null
                            )

                            Text(
                                text = stringResource(R.string.top_pick_rationale),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = gptInvestorColors.utilColors.borderBright10,
                            contentColor = gptInvestorColors.greenColors.allGreen
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                text = "Score: ${state.topPick?.confidenceScore}/10",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                    Column {
                        Text(
                            text = state.topPick?.rationale ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                        TextButton(onClick = {
                            onEvent(TopPickEvent.Authenticate(showDialog = true))
                        }) {
                            Text(text = "Read more")
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                // rationale card
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.ic_top_pick_rationale),
                                    contentDescription = null
                                )

                                Text(
                                    text = stringResource(R.string.top_pick_rationale),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = gptInvestorColors.utilColors.borderBright10,
                                contentColor = gptInvestorColors.greenColors.allGreen
                            ) {
                                Text(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    text = "Score: ${state.topPick?.confidenceScore}/10",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                        if (state.isLoggedIn) {
                            ExpandableText(
                                text = state.topPick?.rationale ?: "",
                                collapsedMaxLine = 4,
                                style = MaterialTheme.typography.bodyMedium,
                                showMoreText = stringResource(R.string.read_more),
                                showMoreStyle = SpanStyle(
                                    textDecoration = TextDecoration.Underline,
                                    fontStyle = MaterialTheme.typography.linkMedium.fontStyle,
                                    fontWeight = FontWeight.W500
                                ),
                                showLessText = stringResource(R.string.read_less),
                                showLessStyle = SpanStyle(
                                    textDecoration = TextDecoration.Underline,
                                    fontStyle = MaterialTheme.typography.linkMedium.fontStyle,
                                    fontWeight = FontWeight.W500
                                )
                            )

                            // Metrics
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.List,
                                        contentDescription = null
                                    )
                                    Text(
                                        text = stringResource(R.string.key_metrics),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                state.topPick?.let { topPick ->
                                    topPick.metrics.forEach { metric ->
                                        Text(
                                            text = metric
                                        )
                                    }
                                }
                            }

                            // Risks
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null
                                    )
                                    Text(
                                        text = stringResource(R.string.key_risks),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                state.topPick?.risks.let { risks ->
                                    risks?.forEach { risk ->
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.padding(start = 16.dp)
                                        ) {
                                            Text(
                                                text = "•"
                                            )
                                            Text(
                                                text = risk
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            Column {
                                Text(
                                    text = state.topPick?.rationale ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )
                                TextButton(onClick = {
                                    onEvent(TopPickEvent.Authenticate(showDialog = true))
                                }) {
                                    Text(text = "Read more")
                                }
                            }
                        }
                    }
                }

                // company detail
                if (state.isLoggedIn) {
                    state.companyPresentation?.let { company ->
                        CompanyDetailTab(
                            modifier = Modifier
                                .fillMaxWidth(),
                            company = company,
                            onClickNews = {
                            },
                            onClickSources = {
                                onEvent(TopPickEvent.ClickNewsSources(show = true))
                            }
                        )
                    }
                }
            }
        }
    }
}
