package com.thejawnpaul.gptinvestor.features.search.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.back
import com.thejawnpaul.gptinvestor.clear_search
import com.thejawnpaul.gptinvestor.search_a_stock_to_analyse
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiIconButton
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiSearchField
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchTopBar(
    query: String,
    focusRequester: FocusRequester,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(start = GiSpace.gutter, end = GiSpace.gutter, top = GiSpace.x2, bottom = GiSpace.x3),
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x2),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GiIconButton(
            icon = GiIcons.caretLeft,
            contentDescription = stringResource(Res.string.back),
            onClick = onBack
        )
        GiSearchField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = stringResource(Res.string.search_a_stock_to_analyse),
            clearContentDescription = stringResource(Res.string.clear_search),
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester),
            onSearch = onSearch
        )
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun SearchTopBarPreview() {
    GPTInvestorTheme {
        SearchTopBar(
            query = "nvi",
            focusRequester = FocusRequester(),
            onQueryChange = {},
            onSearch = {},
            onBack = {},
            modifier = Modifier.background(GiTheme.colors.bg)
        )
    }
}
