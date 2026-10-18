package com.thejawnpaul.gptinvestor.features.premium.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.close
import com.thejawnpaul.gptinvestor.features.premium.presentation.state.PremiumSheetState
import com.thejawnpaul.gptinvestor.premium_billed_info
import com.thejawnpaul.gptinvestor.premium_feature_coverage_desc
import com.thejawnpaul.gptinvestor.premium_feature_coverage_title
import com.thejawnpaul.gptinvestor.premium_feature_queries_desc
import com.thejawnpaul.gptinvestor.premium_feature_queries_title
import com.thejawnpaul.gptinvestor.premium_feature_research_desc
import com.thejawnpaul.gptinvestor.premium_feature_research_title
import com.thejawnpaul.gptinvestor.premium_sheet_subtitle
import com.thejawnpaul.gptinvestor.premium_sheet_subtitle_watchlist_full
import com.thejawnpaul.gptinvestor.premium_sheet_title
import com.thejawnpaul.gptinvestor.subscribe_now
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.components.GiBottomSheet
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiButtonSize
import com.thejawnpaul.gptinvestor.theme.components.GiButtonVariant
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiPerkRow
import com.thejawnpaul.gptinvestor.theme.components.GiProTag
import com.thejawnpaul.gptinvestor.theme.components.GiProTagTone
import com.thejawnpaul.gptinvestor.theme.components.GiStepBar
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle
import com.thejawnpaul.gptinvestor.watchlist_full_add_more
import com.thejawnpaul.gptinvestor.watchlist_full_add_ticker
import com.thejawnpaul.gptinvestor.watchlist_slots_used
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumSheet(
    state: PremiumSheetState,
    onDismiss: () -> Unit,
    onPurchase: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cap = state.watchlistCap
    GiBottomSheet(
        onDismissRequest = onDismiss,
        title = stringResource(Res.string.premium_sheet_title),
        closeContentDescription = stringResource(Res.string.close),
        modifier = modifier,
        eyebrow = { GiProTag(tone = GiProTagTone.Standalone) },
        subtitle = if (cap != null) {
            stringResource(Res.string.premium_sheet_subtitle_watchlist_full, cap, cap)
        } else {
            stringResource(Res.string.premium_sheet_subtitle)
        }
    ) {
        if (cap != null) {
            WatchlistSlotMeter(cap = cap, ticker = state.ticker)
        }
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x4)) {
            GiPerkRow(
                icon = GiIcons.infinity,
                title = stringResource(Res.string.premium_feature_queries_title),
                subtitle = stringResource(Res.string.premium_feature_queries_desc)
            )
            GiPerkRow(
                icon = GiIcons.newspaper,
                title = stringResource(Res.string.premium_feature_coverage_title),
                subtitle = stringResource(Res.string.premium_feature_coverage_desc)
            )
            GiPerkRow(
                icon = GiIcons.star,
                title = stringResource(Res.string.premium_feature_research_title),
                subtitle = stringResource(Res.string.premium_feature_research_desc)
            )
        }
        Column(
            modifier = Modifier.padding(top = GiSpace.x2),
            verticalArrangement = Arrangement.spacedBy(GiSpace.x2)
        ) {
            GiButton(
                text = stringResource(Res.string.subscribe_now),
                onClick = onPurchase,
                modifier = Modifier.fillMaxWidth(),
                size = GiButtonSize.Large,
                variant = GiButtonVariant.Primary,
                loading = state.isPurchasing,
                proPulse = true
            )
            GiText(
                text = stringResource(Res.string.premium_billed_info),
                modifier = Modifier.fillMaxWidth(),
                style = GiTextStyle.Meta,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun WatchlistSlotMeter(cap: Int, ticker: String?, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
        if (cap <= MAX_METER_SEGMENTS) {
            GiStepBar(
                count = cap,
                current = cap - 1,
                contentDescription = stringResource(Res.string.watchlist_slots_used, cap, cap)
            )
        }
        GiText(
            text = if (!ticker.isNullOrBlank()) {
                stringResource(Res.string.watchlist_full_add_ticker, ticker)
            } else {
                stringResource(Res.string.watchlist_full_add_more)
            },
            style = GiTextStyle.Meta
        )
    }
}

private const val MAX_METER_SEGMENTS = 10
