package com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui

import androidx.compose.foundation.BorderStroke
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
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.viewmodel.TidbitAction
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.viewmodel.TidbitDetailState
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.viewmodel.TidbitEvent
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import com.thejawnpaul.gptinvestor.theme.bodyChatBody

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TidbitDetailScreen(modifier: Modifier = Modifier, tidbitId: String, state: TidbitDetailState, onEvent: (TidbitEvent) -> Unit, onAction: (TidbitAction) -> Unit) {
    LaunchedEffect(tidbitId) {
        onEvent(TidbitEvent.GetTidbit(tidbitId))
    }
    val gptInvestorColors = LocalGPTInvestorColors.current

    Scaffold(
        modifier = modifier,
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = {
                    onEvent(TidbitEvent.GoBack)
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = stringResource(id = R.string.back)
                    )
                }
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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

                    val bookmarkIcon = if (true) {
                        R.drawable.ic_bookmark_filled
                    } else {
                        R.drawable.ic_bookmark
                    }

                    // Like
                    IconButton(
                        modifier = Modifier.size(32.dp),
                        onClick = {
                            if (isLiked.value) {
                            } else {
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
                    /*IconButton(
                        modifier = Modifier.size(32.dp),
                        onClick = {

                        }
                    ) {
                        Icon(
                            painter = painterResource(id = bookmarkIcon),
                            contentDescription = stringResource(R.string.like_chosen)
                        )
                    }*/

                    // Share
                    IconButton(
                        modifier = Modifier.size(32.dp),
                        onClick = {
                            onEvent(TidbitEvent.OnClickShare)
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_top_pick_send),
                            contentDescription = null
                        )
                    }
                }

                // Sources
                Surface(
                    shape = RoundedCornerShape(corner = CornerSize(20.dp)),
                    border = BorderStroke(
                        width = 2.dp,
                        color = gptInvestorColors.utilColors.borderBright10
                    ),
                    onClick = { onEvent(TidbitEvent.OnClickSource) }
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
    ) { paddingValues ->
        Box(modifier = modifier.padding(paddingValues)) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
            ContentView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                state = state
            )
        }
    }
}

@Composable
private fun ContentView(modifier: Modifier = Modifier, state: TidbitDetailState) {
    val gptInvestorColors = LocalGPTInvestorColors.current
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(193.dp)
                .padding(horizontal = 0.dp),
            shape = RoundedCornerShape(corner = CornerSize(20.dp))
        ) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = state.mediaUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier,
                text = state.originalAuthor,
                style = MaterialTheme.typography.bodyChatBody
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = gptInvestorColors.accentColors.allAccent20,
                    contentColor = gptInvestorColors.accentColors.allAccent
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        text = state.category,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.W400,
                            lineHeight = 16.sp,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
                /*Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = gptInvestorColors.greenColors.allGreen10,
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        text = "+1",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.W400,
                            lineHeight = 16.sp,
                            letterSpacing = 0.5.sp
                        )
                    )
                }*/
            }
        }

        Text(text = state.title, style = MaterialTheme.typography.titleLarge)

        Text(text = state.content, style = MaterialTheme.typography.bodyMedium)
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
                previewUrl = "",
                title = "The psychology of money",
                content = "This teaches us to be the best person in the world",
                originalAuthor = "John Doe",
                category = "Investing 101"
            )
        )
    }
}
