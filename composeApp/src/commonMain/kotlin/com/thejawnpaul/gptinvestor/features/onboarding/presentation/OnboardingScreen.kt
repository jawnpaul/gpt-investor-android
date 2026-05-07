package com.thejawnpaul.gptinvestor.features.onboarding.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.back
import com.thejawnpaul.gptinvestor.features.company.domain.model.BriefSection
import com.thejawnpaul.gptinvestor.features.company.domain.model.BriefSentiment
import com.thejawnpaul.gptinvestor.features.company.domain.model.BriefTone
import com.thejawnpaul.gptinvestor.features.company.domain.model.CompanyBrief
import com.thejawnpaul.gptinvestor.features.company.domain.model.KeyNumber
import com.thejawnpaul.gptinvestor.features.company.domain.model.KeyNumberType
import com.thejawnpaul.gptinvestor.features.company.presentation.model.CompanyPresentation
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.SingleCompanyItem
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.BriefCard
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.BriefSummaryCard
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.CompanyBriefHeader
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.CompanyBriefSkeleton
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.KeyNumbersCard
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.RiskOpportunityCard
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.SentimentBadge
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.WhatsHappeningCard
import com.thejawnpaul.gptinvestor.features.discover.SearchBarCustom
import com.thejawnpaul.gptinvestor.features.onboarding.presentation.state.BriefView
import com.thejawnpaul.gptinvestor.features.onboarding.presentation.state.OnboardingUiState
import com.thejawnpaul.gptinvestor.features.onboarding.presentation.state.SuggestedStock
import com.thejawnpaul.gptinvestor.gpt_investor
import com.thejawnpaul.gptinvestor.input_logo
import com.thejawnpaul.gptinvestor.mesh_background
import com.thejawnpaul.gptinvestor.onboarding_body_value_prop
import com.thejawnpaul.gptinvestor.onboarding_bullet_follow_up
import com.thejawnpaul.gptinvestor.onboarding_bullet_plain_english
import com.thejawnpaul.gptinvestor.onboarding_bullet_real_time
import com.thejawnpaul.gptinvestor.onboarding_cta_get_started
import com.thejawnpaul.gptinvestor.onboarding_cta_go_to_app
import com.thejawnpaul.gptinvestor.onboarding_cta_start_exploring
import com.thejawnpaul.gptinvestor.onboarding_cta_try_it_now
import com.thejawnpaul.gptinvestor.onboarding_error_message
import com.thejawnpaul.gptinvestor.onboarding_headline_how_it_works
import com.thejawnpaul.gptinvestor.onboarding_headline_stock_selection
import com.thejawnpaul.gptinvestor.onboarding_headline_value_prop
import com.thejawnpaul.gptinvestor.onboarding_loading_headline
import com.thejawnpaul.gptinvestor.onboarding_search_placeholder
import com.thejawnpaul.gptinvestor.onboarding_skip_for_now
import com.thejawnpaul.gptinvestor.onboarding_subtext_stock_selection
import com.thejawnpaul.gptinvestor.onboarding_success_body
import com.thejawnpaul.gptinvestor.onboarding_success_headline
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.linkMedium
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val EaseOutQuart = CubicBezierEasing(0.25f, 1f, 0.5f, 1f)
private val EaseInOutQuart = CubicBezierEasing(0.76f, 0f, 0.24f, 1f)

@Composable
fun OnboardingScreen(
    state: OnboardingUiState,
    searchResults: Flow<PagingData<CompanyPresentation>>,
    onNextScreen: () -> Unit,
    onSkip: (Int) -> Unit,
    onSelectStock: (ticker: String, companyName: String, source: String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onBackToStockSelection: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = state.currentScreen,
        modifier = modifier,
        transitionSpec = {
            if (targetState > initialState) {
                slideInHorizontally(tween(280, easing = EaseOutQuart)) { it } +
                    fadeIn(tween(220, easing = EaseOutQuart)) togetherWith
                    slideOutHorizontally(tween(220)) { -it } +
                    fadeOut(tween(150))
            } else {
                slideInHorizontally(tween(280, easing = EaseOutQuart)) { -it } +
                    fadeIn(tween(220, easing = EaseOutQuart)) togetherWith
                    slideOutHorizontally(tween(220)) { it } +
                    fadeOut(tween(150))
            }
        },
        label = "onboardingScreen"
    ) { screen ->
        when (screen) {
            0 -> ValuePropositionScreen(
                onStart = onNextScreen,
                onSkip = { onSkip(0) }
            )
            1 -> HowItWorksScreen(
                onTryItNow = onNextScreen,
                onSkip = { onSkip(1) }
            )
            2 -> StockSelectionScreen(
                state = state,
                searchResults = searchResults,
                onSelectStock = onSelectStock,
                onSearchQueryChange = onSearchQueryChange
            )
            3 -> LiveBriefScreen(
                state = state,
                onBack = onBackToStockSelection,
                onFinish = onFinish
            )
        }
    }
}

@Composable
private fun SkipText(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val skipAlpha by animateFloatAsState(
        targetValue = if (pressed) 0.5f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium),
        label = "skipAlpha"
    )
    Text(
        modifier = modifier
            .graphicsLayer { alpha = skipAlpha }
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        text = text,
        textDecoration = TextDecoration.Underline,
        style = MaterialTheme.typography.linkMedium
    )
}

@Composable
private fun ValuePropositionScreen(onStart: () -> Unit, onSkip: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(Res.drawable.mesh_background),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 160.dp)
                    .fillMaxHeight(0.4f)
                    .align(Alignment.BottomCenter)
                    .alpha(0.4f)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1.2f))

                Image(
                    painter = painterResource(Res.drawable.input_logo),
                    contentDescription = stringResource(Res.string.gpt_investor),
                    modifier = Modifier.size(72.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(Res.string.onboarding_headline_value_prop),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(Res.string.onboarding_body_value_prop),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(corner = CornerSize(20.dp)),
                    onClick = onStart
                ) {
                    Text(
                        text = stringResource(Res.string.onboarding_cta_get_started),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                SkipText(
                    text = stringResource(Res.string.onboarding_skip_for_now),
                    onClick = onSkip
                )

                Spacer(modifier = Modifier.weight(0.4f))
            }
        }
    }
}

@Composable
private fun HowItWorksScreen(onTryItNow: () -> Unit, onSkip: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.5f))

            BriefCardMock()

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(Res.string.onboarding_headline_how_it_works),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BulletRow(stringResource(Res.string.onboarding_bullet_plain_english))
                BulletRow(stringResource(Res.string.onboarding_bullet_real_time))
                BulletRow(stringResource(Res.string.onboarding_bullet_follow_up))
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(corner = CornerSize(20.dp)),
                onClick = onTryItNow
            ) {
                Text(
                    text = stringResource(Res.string.onboarding_cta_try_it_now),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SkipText(
                text = stringResource(Res.string.onboarding_skip_for_now),
                onClick = onSkip
            )

            Spacer(modifier = Modifier.weight(0.3f))
        }
    }
}

@Composable
private fun BriefCardMock(modifier: Modifier = Modifier) {
    BriefCard(modifier = modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            MockPlaceholderRow(widthFraction = 0.5f, height = 14.dp)
            MockPlaceholderRow(widthFraction = 1f, height = 10.dp)
            MockPlaceholderRow(widthFraction = 0.8f, height = 10.dp)
            Spacer(modifier = Modifier.height(4.dp))
            MockPlaceholderRow(widthFraction = 1f, height = 10.dp)
            MockPlaceholderRow(widthFraction = 0.9f, height = 10.dp)
            MockPlaceholderRow(widthFraction = 0.7f, height = 10.dp)
        }
    }
}

@Composable
private fun MockPlaceholderRow(widthFraction: Float, height: Dp, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.12f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = EaseInOutQuart),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )
    Surface(
        modifier = modifier
            .fillMaxWidth(widthFraction)
            .height(height),
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = shimmerAlpha)
    ) {}
}

@Composable
private fun BulletRow(text: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth()) {
        Text(text = "✅", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun StockSelectionScreen(
    state: OnboardingUiState,
    searchResults: Flow<PagingData<CompanyPresentation>>,
    onSelectStock: (ticker: String, companyName: String, source: String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagingItems = searchResults.collectAsLazyPagingItems()

    Scaffold(modifier = modifier) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(Res.string.onboarding_headline_stock_selection),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(Res.string.onboarding_subtext_stock_selection),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            SuggestedStocksGrid(
                stocks = state.suggestedStocks,
                onSelectStock = { stock ->
                    onSelectStock(stock.ticker, stock.name, "suggested")
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            SearchBarCustom(
                query = state.searchQuery,
                placeHolder = stringResource(Res.string.onboarding_search_placeholder),
                onClose = { onSearchQueryChange("") },
                onQueryChange = onSearchQueryChange,
                onSearch = {},
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(
                    count = pagingItems.itemCount,
                    key = pagingItems.itemKey { it.ticker }
                ) { index ->
                    val company = pagingItems[index]
                    if (company != null) {
                        SingleCompanyItem(
                            company = company,
                            onClick = { ticker ->
                                onSelectStock(ticker, company.name, "search")
                            },
                            modifier = Modifier.animateItem(
                                fadeInSpec = tween(220, easing = EaseOutQuart),
                                placementSpec = spring(stiffness = Spring.StiffnessMediumLow),
                                fadeOutSpec = tween(150)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SuggestedStocksGrid(
    stocks: List<SuggestedStock>,
    onSelectStock: (SuggestedStock) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        stocks.chunked(3).forEach { rowStocks ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowStocks.forEach { stock ->
                    SuggestedStockChip(
                        stock = stock,
                        onClick = { onSelectStock(stock) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SuggestedStockChip(stock: SuggestedStock, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val haptics = LocalHapticFeedback.current
    val chipScale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium),
        label = "chipScale"
    )

    Surface(
        modifier = modifier
            .graphicsLayer {
                scaleX = chipScale
                scaleY = chipScale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick()
                }
            ),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (stock.logoUrl.isNotBlank()) {
                AsyncImage(
                    model = stock.logoUrl,
                    contentDescription = stock.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                )
            } else {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = stock.ticker.take(1),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text(
                text = stock.ticker,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = stock.name,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun LiveBriefScreen(
    state: OnboardingUiState,
    onBack: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val companyName = state.selectedCompanyName.orEmpty()

    Scaffold(
        modifier = modifier,
        topBar = {
            IconButton(modifier = Modifier.statusBarsPadding(), onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(Res.string.back)
                )
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = state.briefView,
            transitionSpec = {
                fadeIn(tween(300, easing = EaseOutQuart)) togetherWith fadeOut(tween(150))
            },
            modifier = Modifier.fillMaxSize(),
            label = "briefView"
        ) { briefView ->
            when (briefView) {
                is BriefView.Loading -> {
                    LazyColumn(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = stringResource(Res.string.onboarding_loading_headline, companyName),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        item { CompanyBriefSkeleton(modifier = Modifier.fillMaxWidth()) }
                    }
                }

                is BriefView.Success -> {
                    val brief = briefView.brief
                    LazyColumn(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = stringResource(Res.string.onboarding_success_headline, companyName),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        item { CompanyBriefHeader(brief = brief) }

                        brief.sentiment?.let { sentiment ->
                            item {
                                SentimentBadge(
                                    sentiment = sentiment,
                                    summary = brief.sentimentSummary
                                )
                            }
                        }

                        brief.summary?.let { summary ->
                            item { BriefSummaryCard(summary = summary) }
                        }

                        if (brief.keyNumbers.isNotEmpty()) {
                            item { KeyNumbersCard(keyNumbers = brief.keyNumbers) }
                        }

                        if (brief.news.isNotEmpty()) {
                            item {
                                WhatsHappeningCard(
                                    news = brief.news,
                                    onNewsClick = {}
                                )
                            }
                        }

                        if (brief.risk != null || brief.opportunity != null) {
                            item {
                                RiskOpportunityCard(
                                    risk = brief.risk,
                                    opportunity = brief.opportunity
                                )
                            }
                        }

                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(Res.string.onboarding_success_body),
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp),
                                    shape = RoundedCornerShape(corner = CornerSize(20.dp)),
                                    onClick = onFinish
                                ) {
                                    Text(
                                        text = stringResource(Res.string.onboarding_cta_start_exploring),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                        }
                    }
                }

                is BriefView.Error -> {
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(Res.string.onboarding_error_message),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(corner = CornerSize(20.dp)),
                            onClick = onFinish
                        ) {
                            Text(
                                text = stringResource(Res.string.onboarding_cta_go_to_app),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun OnboardingScreen_ValueProp_Preview() {
    GPTInvestorTheme {
        Surface {
            OnboardingScreen(
                state = OnboardingUiState(currentScreen = 0),
                searchResults = flowOf(PagingData.from(emptyList())),
                onNextScreen = {},
                onSkip = {},
                onSelectStock = { _, _, _ -> },
                onSearchQueryChange = {},
                onBackToStockSelection = {},
                onFinish = {}
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun OnboardingScreen_HowItWorks_Preview() {
    GPTInvestorTheme {
        Surface {
            OnboardingScreen(
                state = OnboardingUiState(currentScreen = 1),
                searchResults = flowOf(PagingData.from(emptyList())),
                onNextScreen = {},
                onSkip = {},
                onSelectStock = { _, _, _ -> },
                onSearchQueryChange = {},
                onBackToStockSelection = {},
                onFinish = {}
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun OnboardingScreen_StockSelection_Preview() {
    GPTInvestorTheme {
        Surface {
            OnboardingScreen(
                state = OnboardingUiState(currentScreen = 2),
                searchResults = flowOf(PagingData.from(emptyList())),
                onNextScreen = {},
                onSkip = {},
                onSelectStock = { _, _, _ -> },
                onSearchQueryChange = {},
                onBackToStockSelection = {},
                onFinish = {}
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun OnboardingScreen_LiveBrief_Preview() {
    val sampleBrief = CompanyBrief(
        ticker = "AAPL",
        name = "Apple Inc.",
        logoUrl = "",
        price = 220.50f,
        change = 1.2f,
        sentiment = BriefSentiment.Bullish,
        sentimentSummary = "Apple's outlook remains strong due to AI integration.",
        summary = "Apple Inc. is showing strong performance in its services sector.",
        keyNumbers = listOf(
            KeyNumber(KeyNumberType.MarketCap, "$3.5T", "Largest market cap", BriefTone.Positive),
            KeyNumber(KeyNumberType.PeRatio, "30.5", "Fairly valued", BriefTone.Neutral)
        ),
        news = emptyList(),
        risk = BriefSection("Regulatory risk", "Ongoing antitrust investigations in multiple regions."),
        opportunity = BriefSection("AI Growth", "Integration of AI across the product lineup.")
    )

    GPTInvestorTheme {
        Surface {
            OnboardingScreen(
                state = OnboardingUiState(
                    currentScreen = 3,
                    selectedCompanyName = "Apple Inc.",
                    briefView = BriefView.Success(sampleBrief)
                ),
                searchResults = flowOf(PagingData.from(emptyList())),
                onNextScreen = {},
                onSkip = {},
                onSelectStock = { _, _, _ -> },
                onSearchQueryChange = {},
                onBackToStockSelection = {},
                onFinish = {}
            )
        }
    }
}
