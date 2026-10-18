package com.thejawnpaul.gptinvestor.features.toppick.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui.ContentEmptyState
import com.thejawnpaul.gptinvestor.features.toppick.presentation.state.TopPicksView
import com.thejawnpaul.gptinvestor.saved_count
import com.thejawnpaul.gptinvestor.saved_picks
import com.thejawnpaul.gptinvestor.saved_picks_empty_body
import com.thejawnpaul.gptinvestor.saved_picks_empty_title
import com.thejawnpaul.gptinvestor.see_todays_picks
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import org.jetbrains.compose.resources.stringResource

@Composable
fun SavedTopPicksScreen(
    state: TopPicksView,
    onGoBack: () -> Unit,
    onGoToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    onSeeTodaysPicks: (() -> Unit)? = null
) {
    val count = state.topPicks.size
    TopPickListScaffold(
        title = stringResource(Res.string.saved_picks),
        subtitle = if (count > 0) stringResource(Res.string.saved_count, count) else null,
        state = state,
        onGoBack = onGoBack,
        onGoToDetail = onGoToDetail,
        modifier = modifier
    ) {
        ContentEmptyState(
            icon = GiIcons.bookmarkSimple,
            title = stringResource(Res.string.saved_picks_empty_title),
            body = stringResource(Res.string.saved_picks_empty_body),
            actionText = stringResource(Res.string.see_todays_picks),
            onAction = onSeeTodaysPicks
        )
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun SavedTopPicksEmptyPreview() {
    GPTInvestorTheme {
        SavedTopPicksScreen(state = TopPicksView(), onGoBack = {}, onGoToDetail = {}, onSeeTodaysPicks = {})
    }
}

@PreviewLightDark
@Composable
private fun SavedTopPicksPreview() {
    GPTInvestorTheme {
        SavedTopPicksScreen(state = TopPicksView(topPicks = listOf(previewPick)), onGoBack = {}, onGoToDetail = {})
    }
}
