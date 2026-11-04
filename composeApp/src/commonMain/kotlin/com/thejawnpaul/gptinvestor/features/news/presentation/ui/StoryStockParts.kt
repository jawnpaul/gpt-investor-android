package com.thejawnpaul.gptinvestor.features.news.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.tickerStock
import com.thejawnpaul.gptinvestor.features.news.domain.model.StoryStock
import com.thejawnpaul.gptinvestor.features.news.presentation.AtStoryLabel
import com.thejawnpaul.gptinvestor.features.news.presentation.NowLabel
import com.thejawnpaul.gptinvestor.features.news.presentation.PriceBadge
import com.thejawnpaul.gptinvestor.features.news.presentation.PriceNote
import com.thejawnpaul.gptinvestor.features.news.presentation.PriceTilesUi
import com.thejawnpaul.gptinvestor.features.news.presentation.StockWatchState
import com.thejawnpaul.gptinvestor.features.news.presentation.WatchAllUi
import com.thejawnpaul.gptinvestor.news_big_move
import com.thejawnpaul.gptinvestor.news_price_at_story_time
import com.thejawnpaul.gptinvestor.news_price_halted
import com.thejawnpaul.gptinvestor.news_price_market_closed
import com.thejawnpaul.gptinvestor.news_price_next_open
import com.thejawnpaul.gptinvestor.news_price_now_since
import com.thejawnpaul.gptinvestor.news_price_now_time
import com.thejawnpaul.gptinvestor.news_price_previous_close
import com.thejawnpaul.gptinvestor.news_price_story_date
import com.thejawnpaul.gptinvestor.news_price_unavailable
import com.thejawnpaul.gptinvestor.news_price_unavailable_updated
import com.thejawnpaul.gptinvestor.news_sheet_at_story
import com.thejawnpaul.gptinvestor.news_sheet_now
import com.thejawnpaul.gptinvestor.news_watch_add
import com.thejawnpaul.gptinvestor.news_watch_all_done
import com.thejawnpaul.gptinvestor.news_watch_all_none
import com.thejawnpaul.gptinvestor.news_watch_all_rest
import com.thejawnpaul.gptinvestor.news_watch_remove
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiButtonVariant
import com.thejawnpaul.gptinvestor.theme.components.GiDeltaPill
import com.thejawnpaul.gptinvestor.theme.components.GiDivider
import com.thejawnpaul.gptinvestor.theme.components.GiIconToggleButton
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiListGroup
import com.thejawnpaul.gptinvestor.theme.components.GiStockRow
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ConnectedStockList(
    stocks: List<StoryStock>,
    watch: StockWatchState,
    onTap: (String) -> Unit,
    onToggleWatch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    GiListGroup(modifier = modifier) {
        stocks.forEachIndexed { index, stock ->
            if (index > 0) GiDivider()
            val watched = watch.isWatched(stock.ticker)
            val pending = watch.isPending(stock.ticker)
            GiStockRow(
                stock = tickerStock(
                    ticker = stock.ticker,
                    title = stock.name,
                    subtitle = stock.reason,
                    percent = stock.price.changePercent,
                    imageUrl = stock.logoUrl
                ),
                onClick = { onTap(stock.ticker) },
                trailing = {
                    WatchStar(
                        name = stock.name,
                        watched = watched,
                        pending = pending,
                        onToggle = { onToggleWatch(stock.ticker) }
                    )
                }
            )
        }
    }
}

@Composable
internal fun WatchStar(name: String, watched: Boolean, pending: Boolean, onToggle: () -> Unit) {
    GiIconToggleButton(
        checked = watched,
        onCheckedChange = { onToggle() },
        icon = GiIcons.star,
        checkedIcon = GiIcons.starFill,
        contentDescription = stringResource(
            if (watched) Res.string.news_watch_remove else Res.string.news_watch_add,
            name
        ),
        loading = pending,
        checkedHaptic = null
    )
}

@Composable
internal fun WatchAllButton(ui: WatchAllUi, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val text = when (ui) {
        is WatchAllUi.AllWatched -> pluralStringResource(Res.plurals.news_watch_all_done, ui.count, ui.count)
        is WatchAllUi.AddAll -> pluralStringResource(Res.plurals.news_watch_all_none, ui.count, ui.count)
        is WatchAllUi.AddRest -> pluralStringResource(Res.plurals.news_watch_all_rest, ui.count, ui.count)
    }
    GiButton(
        text = text,
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        variant = GiButtonVariant.Secondary,
        leadingIcon = if (ui is WatchAllUi.AllWatched) GiIcons.check else GiIcons.plus,
        enabled = ui.enabled
    )
}

@Composable
internal fun StoryPriceTiles(ui: PriceTilesUi, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x2),
            verticalArrangement = Arrangement.spacedBy(GiSpace.x2),
            maxItemsInEachRow = 2
        ) {
            PriceTile(label = ui.atStoryLabel.text(), value = ui.atStoryValue, modifier = Modifier.weight(1f))
            PriceTile(label = ui.nowLabel.text(), value = ui.nowValue, modifier = Modifier.weight(1f)) {
                when (val badge = ui.badge) {
                    PriceBadge.Halted -> HaltedChip()
                    is PriceBadge.Change -> GiDeltaPill(percent = badge.percent)
                    null -> Unit
                }
            }
        }
        ui.note?.let { GiText(text = it.text(), style = GiTextStyle.Meta) }
    }
}

@Composable
private fun AtStoryLabel.text(): String = when (this) {
    is AtStoryLabel.AtTime -> stringResource(Res.string.news_price_at_story_time, time)
    is AtStoryLabel.PreviousClose -> stringResource(Res.string.news_price_previous_close, weekday)
    is AtStoryLabel.StoryDate -> stringResource(Res.string.news_price_story_date, date)
    AtStoryLabel.LastClose -> stringResource(Res.string.news_sheet_at_story)
}

@Composable
private fun NowLabel.text(): String = when (this) {
    is NowLabel.AtTime -> stringResource(Res.string.news_price_now_time, time)
    is NowLabel.SinceDate -> stringResource(Res.string.news_price_now_since, date)
    NowLabel.MarketClosed -> stringResource(Res.string.news_price_market_closed)
    NowLabel.Now -> stringResource(Res.string.news_sheet_now)
}

@Composable
private fun PriceNote.text(): String = when (this) {
    is PriceNote.NextOpen -> stringResource(Res.string.news_price_next_open, time)
    is PriceNote.UnavailableSince -> stringResource(Res.string.news_price_unavailable_updated, time)
    PriceNote.Unavailable -> stringResource(Res.string.news_price_unavailable)
    PriceNote.BigMove -> stringResource(Res.string.news_big_move)
}

@Composable
private fun PriceTile(
    label: String,
    value: String?,
    modifier: Modifier = Modifier,
    trailing: @Composable () -> Unit = {}
) {
    Column(
        modifier = modifier
            .clip(GiRadius.sm)
            .background(GiTheme.colors.tile)
            .padding(horizontal = GiSpace.x4, vertical = GiSpace.x3),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x1)
    ) {
        GiText(text = label, style = GiTextStyle.Meta)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x2),
            verticalArrangement = Arrangement.spacedBy(GiSpace.x1),
            itemVerticalAlignment = Alignment.CenterVertically
        ) {
            GiText(text = value ?: "—", style = GiTextStyle.SectionTitle, color = GiTheme.colors.ink)
            trailing()
        }
    }
}

@Composable
private fun HaltedChip() {
    val colors = GiTheme.colors
    Row(
        modifier = Modifier
            .clip(GiRadius.xs)
            .background(colors.cautionSoft)
            .padding(horizontal = GiSpace.x2, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x1),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = GiIcons.warningCircle,
            contentDescription = null,
            modifier = Modifier.size(12.dp),
            tint = colors.caution
        )
        GiText(text = stringResource(Res.string.news_price_halted), style = GiTextStyle.Tag, color = colors.caution)
    }
}
