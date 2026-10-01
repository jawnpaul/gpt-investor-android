package com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.add
import com.thejawnpaul.gptinvestor.browse_all_companies
import com.thejawnpaul.gptinvestor.build_your_digest
import com.thejawnpaul.gptinvestor.digest_pending_description
import com.thejawnpaul.gptinvestor.digest_pending_description_premium
import com.thejawnpaul.gptinvestor.digest_pending_notify_subtitle
import com.thejawnpaul.gptinvestor.digest_pending_notify_title
import com.thejawnpaul.gptinvestor.digest_pending_title
import com.thejawnpaul.gptinvestor.done
import com.thejawnpaul.gptinvestor.features.component.ShimmerBox
import com.thejawnpaul.gptinvestor.features.digest.data.remote.model.RecommendedDigestCompany
import com.thejawnpaul.gptinvestor.features.digest.presentation.ui.DailyDigestStatus
import com.thejawnpaul.gptinvestor.features.digest.presentation.ui.DailyDigestView
import com.thejawnpaul.gptinvestor.features.digest.presentation.ui.StockDigestPresentation
import com.thejawnpaul.gptinvestor.generated_at
import com.thejawnpaul.gptinvestor.ic_calendar
import com.thejawnpaul.gptinvestor.ic_clock
import com.thejawnpaul.gptinvestor.ic_lock
import com.thejawnpaul.gptinvestor.ic_notification_bell
import com.thejawnpaul.gptinvestor.ic_search
import com.thejawnpaul.gptinvestor.improved
import com.thejawnpaul.gptinvestor.key_event
import com.thejawnpaul.gptinvestor.locked_stock_premium_upsell
import com.thejawnpaul.gptinvestor.morning_brief
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import com.thejawnpaul.gptinvestor.today_s_digest
import com.thejawnpaul.gptinvestor.unlock
import com.thejawnpaul.gptinvestor.x_more_stocks_are_locked
import com.thejawnpaul.gptinvestor.x_more_stocks_in_your_digest
import com.thejawnpaul.gptinvestor.yesterday_s_digest_today_s_is_on_the_way
import com.thejawnpaul.gptinvestor.you_re_not_tracking_any_companies_yet
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val EaseOutQuart = CubicBezierEasing(0.25f, 1f, 0.5f, 1f)
private val EaseInOutQuart = CubicBezierEasing(0.76f, 0f, 0.24f, 1f)

@Composable
fun DailyDigestCard(
    view: DailyDigestView,
    onAddStock: (String) -> Unit,
    onBrowseAll: () -> Unit,
    onSeeFullDigest: () -> Unit,
    onUnlockPremiumPending: () -> Unit,
    onUnlockPremiumReady: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(Res.drawable.ic_calendar),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(Res.string.morning_brief),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(
                    if (view.status ==
                        DailyDigestStatus.EMPTY
                    ) {
                        Res.string.build_your_digest
                    } else {
                        Res.string.today_s_digest
                    }
                ),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        if (view.isStale && view.status == DailyDigestStatus.READY) {
            Spacer(Modifier.height(8.dp))
            Surface(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_clock),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(Res.string.yesterday_s_digest_today_s_is_on_the_way),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        val displayState = when {
            view.loading -> 0
            view.status == DailyDigestStatus.EMPTY -> 1
            view.status == DailyDigestStatus.PENDING -> 2
            view.status == DailyDigestStatus.READY -> 3
            else -> 1
        }
        Surface(
            onClick = { if (displayState == 3) onSeeFullDigest() },
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            AnimatedContent(
                targetState = displayState,
                transitionSpec = {
                    fadeIn(tween(220, easing = EaseOutQuart)) togetherWith fadeOut(
                        tween(150, easing = EaseInOutQuart)
                    )
                },
                label = "DailyDigestContent"
            ) { state ->
                when (state) {
                    0 -> DailyDigestShimmer()
                    1 -> EmptyDigestContent(
                        view = view,
                        onAddStock = onAddStock,
                        onBrowseAll = onBrowseAll
                    )

                    2 -> PendingDigestContent(
                        isPremium = view.isPremium,
                        nextHourLabel = view.nextHourLabel,
                        onUnlockPremium = onUnlockPremiumPending
                    )

                    3 -> ReadyDigestContent(
                        stocks = view.stocks,
                        lockedStocksCount = view.lockedStocksCount,
                        lockedStocksSummary = view.lockedStocksSummary,
                        lastUpdated = view.lastUpdated,
                        onSeeFullDigest = onSeeFullDigest,
                        onUnlockPremium = onUnlockPremiumReady
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyDigestContent(view: DailyDigestView, onAddStock: (String) -> Unit, onBrowseAll: () -> Unit) {
    val customColors = LocalGPTInvestorColors.current

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = stringResource(Res.string.you_re_not_tracking_any_companies_yet),
            style = MaterialTheme.typography.bodyMedium,
            color = customColors.textColors.secondary50
        )
        Spacer(Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .border(1.dp, customColors.utilColors.borderBright10, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
        ) {
            view.suggestedStocks.forEachIndexed { index, stock ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stock.ticker,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stock.companyName,
                            style = MaterialTheme.typography.bodySmall,
                            color = customColors.textColors.secondary50
                        )
                    }

                    val isAdding = view.addingTickers.contains(stock.ticker)
                    val isAdded = view.addedTickers.contains(stock.ticker)
                    val addInteractionSource = remember { MutableInteractionSource() }
                    val addPressed by addInteractionSource.collectIsPressedAsState()
                    val addScale by animateFloatAsState(
                        targetValue = if (addPressed) 0.95f else 1f,
                        animationSpec = spring(stiffness = Spring.StiffnessMedium),
                        label = "addButtonScale"
                    )

                    Surface(
                        onClick = { onAddStock(stock.ticker) },
                        enabled = !isAdding && !isAdded,
                        shape = RoundedCornerShape(20.dp),
                        color = when {
                            isAdded -> customColors.greenColors.allGreen10
                            isAdding -> Color.Transparent
                            else -> customColors.accentColors.allAccent20
                        },
                        interactionSource = addInteractionSource,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .graphicsLayer {
                                scaleX = addScale
                                scaleY = addScale
                            }
                    ) {
                        AnimatedContent(
                            targetState = when {
                                isAdding -> 0
                                isAdded -> 1
                                else -> 2
                            },
                            transitionSpec = {
                                fadeIn(
                                    tween(
                                        200,
                                        easing = EaseOutQuart
                                    )
                                ) togetherWith fadeOut(tween(150, easing = EaseInOutQuart))
                            },
                            label = "AddButtonState"
                        ) { state ->
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                when (state) {
                                    0 -> {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp,
                                            color = customColors.accentColors.allAccent
                                        )
                                    }

                                    1 -> {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = customColors.greenColors.allGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = stringResource(Res.string.done),
                                            style = MaterialTheme.typography.labelLarge,
                                            color = customColors.greenColors.allGreen,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    2 -> {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            tint = customColors.accentColors.allAccent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = stringResource(Res.string.add),
                                            style = MaterialTheme.typography.labelLarge,
                                            color = customColors.accentColors.allAccent,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                if (index < view.suggestedStocks.size - 1) {
                    HorizontalDivider(color = customColors.utilColors.borderBright10)
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        val browseAllInteractionSource = remember { MutableInteractionSource() }
        val browseAllPressed by browseAllInteractionSource.collectIsPressedAsState()
        val browseAllScale by animateFloatAsState(
            targetValue = if (browseAllPressed) 0.97f else 1f,
            animationSpec = spring(stiffness = Spring.StiffnessMedium),
            label = "browseAllScale"
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = browseAllScale
                    scaleY = browseAllScale
                },
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, customColors.utilColors.borderBright10),
            onClick = onBrowseAll,
            interactionSource = browseAllInteractionSource
        ) {
            Row(
                modifier = Modifier.padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_search),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(Res.string.browse_all_companies),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun PendingDigestContent(isPremium: Boolean, nextHourLabel: String, onUnlockPremium: () -> Unit) {
    val customColors = LocalGPTInvestorColors.current

    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_search),
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(Res.string.digest_pending_title),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(
                if (isPremium) {
                    Res.string.digest_pending_description_premium
                } else {
                    Res.string.digest_pending_description
                },
                nextHourLabel
            ),
            style = MaterialTheme.typography.bodySmall,
            color = customColors.textColors.secondary50,
            textAlign = TextAlign.Center
        )
        if (!isPremium) {
            Spacer(Modifier.height(20.dp))
            Surface(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = RoundedCornerShape(16.dp),
                onClick = onUnlockPremium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_notification_bell),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(Res.string.digest_pending_notify_title),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = stringResource(Res.string.digest_pending_notify_subtitle),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReadyDigestContent(
    stocks: List<StockDigestPresentation>,
    lockedStocksCount: Int,
    lockedStocksSummary: String?,
    lastUpdated: String?,
    onSeeFullDigest: () -> Unit,
    onUnlockPremium: () -> Unit
) {
    val customColors = LocalGPTInvestorColors.current

    Column(modifier = Modifier.padding(16.dp)) {
        stocks.forEach { stock ->
            StockDigestCard(stock = stock)
            Spacer(Modifier.height(16.dp))
        }

        if (lastUpdated != null) {
            Text(
                text = stringResource(Res.string.generated_at, lastUpdated),
                style = MaterialTheme.typography.labelSmall,
                color = customColors.textColors.secondary50
            )
        }

        val remainingStocks = (stocks.size + lockedStocksCount).minus(1)

        if (remainingStocks > 0) {
            Spacer(Modifier.height(16.dp))

            val seeFullInteractionSource = remember { MutableInteractionSource() }
            val seeFullPressed by seeFullInteractionSource.collectIsPressedAsState()
            val seeFullScale by animateFloatAsState(
                targetValue = if (seeFullPressed) 0.97f else 1f,
                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                label = "seeFullDigestScale"
            )
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        scaleX = seeFullScale
                        scaleY = seeFullScale
                    },
                shape = RoundedCornerShape(12.dp),
                onClick = onSeeFullDigest,
                interactionSource = seeFullInteractionSource
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(
                            Res.string.x_more_stocks_in_your_digest,
                            remainingStocks
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null
                    )
                }
            }
        }

        if (lockedStocksCount > 0) {
            Spacer(Modifier.height(12.dp))
            Surface(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_lock),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(
                                Res.string.x_more_stocks_are_locked,
                                lockedStocksCount
                            ),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        if (lockedStocksSummary != null) {
                            Text(
                                text = stringResource(
                                    Res.string.locked_stock_premium_upsell,
                                    lockedStocksSummary
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                    Button(
                        onClick = onUnlockPremium,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.unlock),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StockDigestCard(stock: StockDigestPresentation) {
    val customColors = LocalGPTInvestorColors.current

    Surface(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, customColors.utilColors.borderBright10),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stock.ticker,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stock.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = customColors.textColors.secondary50
                    )
                }
                if (stock.isImproved) {
                    Surface(
                        color = customColors.greenColors.allGreen10,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(customColors.greenColors.allGreen, CircleShape)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = stringResource(Res.string.improved),
                                style = MaterialTheme.typography.labelSmall,
                                color = customColors.greenColors.allGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = stock.summary,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
            if (stock.keyEvent != null) {
                Spacer(Modifier.height(16.dp))
                Surface(
                    color = customColors.utilColors.allDark2.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(vertical = 12.dp)) {
                        Text(
                            text = stringResource(Res.string.key_event),
                            style = MaterialTheme.typography.labelSmall,
                            color = customColors.textColors.secondary50,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = stock.keyEvent,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyDigestShimmer(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        repeat(2) {
            StockDigestShimmer()
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StockDigestShimmer() {
    val customColors = LocalGPTInvestorColors.current

    Surface(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, customColors.utilColors.borderBright10),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ShimmerBox(width = 50.dp, height = 20.dp)
                    Spacer(Modifier.width(8.dp))
                    ShimmerBox(width = 80.dp, height = 14.dp)
                }
            }
            Spacer(Modifier.height(12.dp))
            ShimmerBox(modifier = Modifier.fillMaxWidth(), height = 14.dp)
            Spacer(Modifier.height(4.dp))
            ShimmerBox(modifier = Modifier.fillMaxWidth(0.7f), height = 14.dp)

            Spacer(Modifier.height(16.dp))
            ShimmerBox(
                modifier = Modifier.fillMaxWidth(),
                height = 48.dp,
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun EmptyDigestPreview() {
    GPTInvestorTheme {
        Surface {
            DailyDigestCard(
                view = DailyDigestView(
                    status = DailyDigestStatus.EMPTY,
                    suggestedStocks = listOf(
                        RecommendedDigestCompany("AAPL", "Apple Inc."),
                        RecommendedDigestCompany("NVDA", "NVIDIA Corp."),
                        RecommendedDigestCompany("TSLA", "Tesla, Inc.")
                    )
                ),
                onAddStock = {},
                onBrowseAll = {},
                onSeeFullDigest = {},
                onUnlockPremiumPending = {},
                onUnlockPremiumReady = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PendingDigestPreview() {
    GPTInvestorTheme {
        Surface {
            DailyDigestCard(
                view = DailyDigestView(status = DailyDigestStatus.PENDING, isPremium = false),
                onAddStock = {},
                onBrowseAll = {},
                onSeeFullDigest = {},
                onUnlockPremiumPending = {},
                onUnlockPremiumReady = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PendingDigestPremiumPreview() {
    GPTInvestorTheme {
        Surface {
            DailyDigestCard(
                view = DailyDigestView(status = DailyDigestStatus.PENDING, isPremium = true),
                onAddStock = {},
                onBrowseAll = {},
                onSeeFullDigest = {},
                onUnlockPremiumPending = {},
                onUnlockPremiumReady = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ReadyDigestPreview() {
    GPTInvestorTheme {
        Surface {
            DailyDigestCard(
                view = DailyDigestView(
                    status = DailyDigestStatus.READY,
                    stocks = listOf(
                        StockDigestPresentation(
                            ticker = "AAPL",
                            name = "Apple Inc.",
                            summary = "Apple firmed up overnight after an upbeat services read and" +
                                " constructive supplier commentary out of Asia. Analyst notes " +
                                "turned more positive into next week's print.",
                            isImproved = true,
                            keyEvent = "iPhone 17 pre-orders beat Street estimates by roughly 12%."
                        )
                    ),
                    lockedStocksCount = 2,
                    lockedStocksSummary = "NVDA · TSLA",
                    lastUpdated = "6:42 AM ET"
                ),
                onAddStock = {},
                onBrowseAll = {},
                onSeeFullDigest = {},
                onUnlockPremiumPending = {},
                onUnlockPremiumReady = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun StaleDigestPreview() {
    GPTInvestorTheme {
        Surface {
            DailyDigestCard(
                view = DailyDigestView(
                    status = DailyDigestStatus.READY,
                    isStale = true,
                    stocks = listOf(
                        StockDigestPresentation(
                            ticker = "AAPL",
                            name = "Apple Inc.",
                            summary = "Apple firmed up overnight after an upbeat services read and constructive " +
                                "supplier commentary out of Asia. Analyst notes turned more positive into the print.",
                            isImproved = true,
                            keyEvent = "iPhone 17 pre-orders beat Street estimates by roughly 12%."
                        )
                    ),
                    lastUpdated = "Yesterday, 6:40 AM ET"
                ),
                onAddStock = {},
                onBrowseAll = {},
                onSeeFullDigest = {},
                onUnlockPremiumPending = {},
                onUnlockPremiumReady = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
