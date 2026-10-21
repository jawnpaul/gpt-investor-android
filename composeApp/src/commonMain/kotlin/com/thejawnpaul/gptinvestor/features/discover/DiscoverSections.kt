package com.thejawnpaul.gptinvestor.features.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.browse_all_companies
import com.thejawnpaul.gptinvestor.browse_stocks_filter_by_sector
import com.thejawnpaul.gptinvestor.discover_loading_picks
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.tickerStock
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.state.HomeTidbitView
import com.thejawnpaul.gptinvestor.features.toppick.presentation.model.TopPickPresentation
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiButtonSize
import com.thejawnpaul.gptinvestor.theme.components.GiButtonVariant
import com.thejawnpaul.gptinvestor.theme.components.GiCard
import com.thejawnpaul.gptinvestor.theme.components.GiIconTile
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiLogoTile
import com.thejawnpaul.gptinvestor.theme.components.GiSkeleton
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle
import com.thejawnpaul.gptinvestor.tidbit_min_read
import com.thejawnpaul.gptinvestor.try_again
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun isDiscoverLargeText(): Boolean = LocalDensity.current.fontScale >= 1.5f

private val TidbitThumbSize = 64.dp

private val RowCardPadding = PaddingValues(horizontal = GiSpace.x4, vertical = GiSpace.x3)

@Composable
internal fun BrowseCompaniesCard(onClick: () -> Unit, modifier: Modifier = Modifier) {
    GiCard(modifier = modifier.fillMaxWidth(), onClick = onClick, contentPadding = RowCardPadding) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GiIconTile(icon = GiIcons.listBullets)
            Column(modifier = Modifier.weight(1f)) {
                GiText(text = stringResource(Res.string.browse_all_companies), style = GiTextStyle.Label)
                GiText(
                    text = stringResource(Res.string.browse_stocks_filter_by_sector),
                    style = GiTextStyle.MetaRelaxed,
                    color = GiTheme.colors.muted
                )
            }
            Caret()
        }
    }
}

@Composable
internal fun TopPickCard(pick: TopPickPresentation, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val stock = tickerStock(
        ticker = pick.ticker,
        title = pick.companyName,
        subtitle = pick.ticker,
        percent = null,
        imageUrl = pick.imageUrl
    )
    GiCard(modifier = modifier.fillMaxWidth(), onClick = onClick) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GiLogoTile(initials = stock.initials, brandColor = stock.brandColor, imageUrl = stock.imageUrl)
            Column(modifier = Modifier.weight(1f)) {
                GiText(text = pick.companyName, style = GiTextStyle.Label)
                GiText(text = pick.ticker, style = GiTextStyle.Meta)
            }
            Caret()
        }
        if (pick.rationale.isNotBlank()) {
            GiText(
                text = pick.rationale,
                style = GiTextStyle.BodySmall,
                maxLines = if (isDiscoverLargeText()) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
internal fun TidbitCard(tidbit: HomeTidbitView, onClick: () -> Unit, modifier: Modifier = Modifier) {
    GiCard(modifier = modifier.fillMaxWidth(), onClick = onClick, contentPadding = PaddingValues(GiSpace.x3)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(TidbitThumbSize)
                    .clip(GiRadius.sm)
                    .background(GiTheme.colors.violetSoft)
                    .clearAndSetSemantics {},
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = GiIcons.mark,
                    contentDescription = null,
                    modifier = Modifier.size(GiIconSize.lg),
                    tint = GiTheme.colors.violet
                )
                if (tidbit.previewUrl.isNotBlank()) {
                    AsyncImage(
                        model = tidbit.previewUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(GiSpace.x1)) {
                GiText(text = stringResource(Res.string.tidbit_min_read), style = GiTextStyle.Meta)
                GiText(text = tidbit.title, style = GiTextStyle.Label)
            }
            Caret()
        }
    }
}

@Composable
internal fun DiscoverSectionError(title: String, body: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    val retry = @Composable {
        GiButton(
            text = stringResource(Res.string.try_again),
            onClick = onRetry,
            size = GiButtonSize.Small,
            variant = GiButtonVariant.Secondary
        )
    }
    val copy = @Composable { copyModifier: Modifier ->
        Row(
            modifier = copyModifier,
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ErrorIconTile()
            Column(modifier = Modifier.weight(1f)) {
                GiText(text = title, style = GiTextStyle.Link, color = GiTheme.colors.ink)
                GiText(text = body, style = GiTextStyle.Meta)
            }
        }
    }
    GiCard(
        modifier = modifier
            .fillMaxWidth()
            .semantics { liveRegion = LiveRegionMode.Polite },
        contentPadding = RowCardPadding
    ) {
        if (isDiscoverLargeText()) {
            copy(Modifier.fillMaxWidth())
            retry()
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
                verticalAlignment = Alignment.CenterVertically
            ) {
                copy(Modifier.weight(1f))
                retry()
            }
        }
    }
}

@Composable
internal fun TopPicksSkeleton(modifier: Modifier = Modifier) {
    val loading = stringResource(Res.string.discover_loading_picks)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = loading
                liveRegion = LiveRegionMode.Polite
            },
        verticalArrangement = Arrangement.spacedBy(GiSpace.x3)
    ) {
        repeat(2) {
            GiCard(modifier = Modifier.fillMaxWidth().clearAndSetSemantics {}) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GiSkeleton(modifier = Modifier.size(GiIconSize.xl))
                    Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
                        GiSkeleton(modifier = Modifier.size(width = 112.dp, height = 14.dp))
                        GiSkeleton(modifier = Modifier.size(width = 80.dp, height = GiSpace.x2_5))
                    }
                }
                GiSkeleton(modifier = Modifier.fillMaxWidth().height(GiSpace.x3))
                GiSkeleton(modifier = Modifier.fillMaxWidth(0.88f).height(GiSpace.x3))
                GiSkeleton(modifier = Modifier.fillMaxWidth(0.64f).height(GiSpace.x3))
            }
        }
    }
}

@Composable
internal fun TidbitSkeleton(modifier: Modifier = Modifier) {
    GiCard(modifier = modifier.fillMaxWidth().clearAndSetSemantics {}, contentPadding = PaddingValues(GiSpace.x3)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GiSkeleton(modifier = Modifier.size(TidbitThumbSize))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
                GiSkeleton(modifier = Modifier.size(width = 96.dp, height = GiSpace.x2_5))
                GiSkeleton(modifier = Modifier.fillMaxWidth().height(14.dp))
                GiSkeleton(modifier = Modifier.fillMaxWidth(0.6f).height(14.dp))
            }
        }
    }
}

@Composable
private fun ErrorIconTile() {
    Box(
        modifier = Modifier
            .size(GiIconSize.xl)
            .clip(GiRadius.sm)
            .background(GiTheme.colors.errorSoft),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = GiIcons.warningCircle,
            contentDescription = null,
            modifier = Modifier.size(GiIconSize.md),
            tint = GiTheme.colors.error
        )
    }
}

@Composable
private fun Caret() {
    Icon(
        imageVector = GiIcons.caretRight,
        contentDescription = null,
        modifier = Modifier.size(GiIconSize.sm),
        tint = GiTheme.colors.muted
    )
}
