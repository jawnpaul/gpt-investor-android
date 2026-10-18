package com.thejawnpaul.gptinvestor.features.search.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.discover_search_error_body
import com.thejawnpaul.gptinvestor.discover_search_error_title
import com.thejawnpaul.gptinvestor.discover_search_error_title_generic
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle
import com.thejawnpaul.gptinvestor.try_again
import org.jetbrains.compose.resources.stringResource

private val ErrorTopOffset = 120.dp

@Composable
fun SearchErrorContent(query: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    val title = if (query.isBlank()) {
        stringResource(Res.string.discover_search_error_title_generic)
    } else {
        stringResource(Res.string.discover_search_error_title, query.trim())
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = GiSpace.gutter)
            .padding(top = ErrorTopOffset, bottom = GiSpace.sectionGap)
            .semantics { liveRegion = LiveRegionMode.Assertive },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(GiSpace.x5)
    ) {
        SearchArtTile(icon = GiIcons.wifiSlash, container = GiTheme.colors.tile, content = GiTheme.colors.muted)
        Column(
            modifier = Modifier.widthIn(max = SearchCopyMaxWidth),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(GiSpace.x2)
        ) {
            GiText(
                text = title,
                modifier = Modifier.semantics { heading() },
                style = GiTextStyle.StatNumber,
                textAlign = TextAlign.Center
            )
            GiText(
                text = stringResource(Res.string.discover_search_error_body),
                style = GiTextStyle.Body,
                textAlign = TextAlign.Center
            )
        }
        GiButton(
            text = stringResource(Res.string.try_again),
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = GiIcons.arrowClockwise
        )
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun SearchErrorContentPreview() {
    GPTInvestorTheme {
        SearchErrorContent(query = "msft", onRetry = {}, modifier = Modifier.background(GiTheme.colors.bg))
    }
}
