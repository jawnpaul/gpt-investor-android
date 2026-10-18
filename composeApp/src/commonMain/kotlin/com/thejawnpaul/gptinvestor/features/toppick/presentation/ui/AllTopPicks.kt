package com.thejawnpaul.gptinvestor.features.toppick.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.back
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui.ContentEmptyState
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui.contentItemMotion
import com.thejawnpaul.gptinvestor.features.toppick.presentation.state.TopPicksView
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiTopBar
import com.thejawnpaul.gptinvestor.top_picks
import com.thejawnpaul.gptinvestor.top_picks_empty_body
import com.thejawnpaul.gptinvestor.top_picks_empty_title
import com.thejawnpaul.gptinvestor.top_picks_loading
import org.jetbrains.compose.resources.stringResource

@Composable
fun AllTopPicksScreen(
    state: TopPicksView,
    onGoBack: () -> Unit,
    onGoToDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TopPickListScaffold(
        title = stringResource(Res.string.top_picks),
        state = state,
        onGoBack = onGoBack,
        onGoToDetail = onGoToDetail,
        modifier = modifier
    ) {
        ContentEmptyState(
            icon = GiIcons.starFill,
            title = stringResource(Res.string.top_picks_empty_title),
            body = stringResource(Res.string.top_picks_empty_body)
        )
    }
}

@Composable
internal fun TopPickListScaffold(
    title: String,
    state: TopPicksView,
    onGoBack: () -> Unit,
    onGoToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    empty: @Composable () -> Unit
) {
    val navBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    Scaffold(
        modifier = modifier,
        containerColor = GiTheme.colors.bg,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            GiTopBar(
                title = title,
                subtitle = subtitle,
                onBack = onGoBack,
                backContentDescription = stringResource(Res.string.back)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(
                start = GiSpace.gutter,
                end = GiSpace.gutter,
                top = GiSpace.x2,
                bottom = GiSpace.x10 + navBottom
            ),
            verticalArrangement = Arrangement.spacedBy(GiSpace.x3)
        ) {
            when {
                state.loading && state.topPicks.isEmpty() -> item(key = "loading") {
                    val loading = stringResource(Res.string.top_picks_loading)
                    Column(
                        modifier = Modifier.semantics {
                            contentDescription = loading
                            liveRegion = LiveRegionMode.Polite
                        },
                        verticalArrangement = Arrangement.spacedBy(GiSpace.x3)
                    ) {
                        repeat(3) { TopPickCardSkeleton() }
                    }
                }

                state.topPicks.isEmpty() -> item(key = "empty") { empty() }

                else -> items(items = state.topPicks, key = { it.id }, contentType = { "pick" }) { pick ->
                    SingleTopPickItem(pickPresentation = pick, onClick = onGoToDetail, modifier = contentItemMotion())
                }
            }
        }
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun AllTopPicksPreview() {
    GPTInvestorTheme {
        AllTopPicksScreen(
            state = TopPicksView(
                topPicks = listOf(previewPick, previewPick.copy(id = "2", companyName = "Broadcom Inc."))
            ),
            onGoBack = {},
            onGoToDetail = {}
        )
    }
}
