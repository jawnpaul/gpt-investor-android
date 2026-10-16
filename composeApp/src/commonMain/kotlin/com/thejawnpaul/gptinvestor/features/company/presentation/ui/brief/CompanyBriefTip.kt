package com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.add_to_watchlist
import com.thejawnpaul.gptinvestor.brief_spotlight_share_desc
import com.thejawnpaul.gptinvestor.brief_spotlight_title
import com.thejawnpaul.gptinvestor.brief_spotlight_watchlist_desc
import com.thejawnpaul.gptinvestor.got_it
import com.thejawnpaul.gptinvestor.next
import com.thejawnpaul.gptinvestor.remove_from_watchlist
import com.thejawnpaul.gptinvestor.share_brief
import com.thejawnpaul.gptinvestor.skip
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiCoachMark
import com.thejawnpaul.gptinvestor.theme.components.GiCoachMarkRow
import com.thejawnpaul.gptinvestor.theme.components.GiIconButton
import com.thejawnpaul.gptinvestor.theme.components.GiIconToggleButton
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import org.jetbrains.compose.resources.stringResource

@Composable
fun CompanyBriefTip(
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    onWatchlist: () -> Unit,
    isWatched: Boolean,
    modifier: Modifier = Modifier
) {
    var step by rememberSaveable { mutableIntStateOf(SHARE_STEP) }
    NavigationBackHandler(
        state = rememberNavigationEventState(currentInfo = NavigationEventInfo.None),
        onBackCompleted = onDismiss
    )
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GiTheme.colors.scrim)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                )
        )
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = GiSpace.x2, end = GiSpace.gutter),
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x2)
        ) {
            if (step == SHARE_STEP) {
                GiIconButton(
                    icon = GiIcons.shareNetwork,
                    contentDescription = stringResource(Res.string.share_brief),
                    onClick = {
                        onShare()
                        onDismiss()
                    }
                )
                Spacer(modifier = Modifier.size(GiSpace.touchTarget))
            } else {
                Spacer(modifier = Modifier.size(GiSpace.touchTarget))
                GiIconToggleButton(
                    checked = isWatched,
                    onCheckedChange = {
                        onWatchlist()
                        onDismiss()
                    },
                    icon = GiIcons.star,
                    checkedIcon = GiIcons.starFill,
                    contentDescription = stringResource(
                        if (isWatched) Res.string.remove_from_watchlist else Res.string.add_to_watchlist
                    )
                )
            }
        }
        AnimatedContent(
            targetState = step,
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = CARD_TOP, start = GiSpace.gutter, end = GiSpace.gutter),
            transitionSpec = {
                fadeIn(tween(GiMotion.BASE, easing = EaseOutQuart)) togetherWith
                    fadeOut(tween(GiMotion.FAST, easing = LinearEasing))
            }
        ) { current ->
            val isShareStep = current == SHARE_STEP
            GiCoachMark(
                title = stringResource(Res.string.brief_spotlight_title),
                actionText = stringResource(if (isShareStep) Res.string.next else Res.string.got_it),
                onAction = { if (isShareStep) step = WATCHLIST_STEP else onDismiss() },
                modifier = Modifier.fillMaxWidth(),
                stepCount = STEP_COUNT,
                currentStep = current,
                secondaryActionText = if (isShareStep) stringResource(Res.string.skip) else null,
                onSecondaryAction = onDismiss,
                caretEndOffset = if (isShareStep) SHARE_CARET_END else STAR_CARET_END
            ) {
                if (isShareStep) {
                    GiCoachMarkRow(
                        icon = GiIcons.shareNetwork,
                        text = stringResource(Res.string.brief_spotlight_share_desc)
                    )
                } else {
                    GiCoachMarkRow(
                        icon = GiIcons.star,
                        text = stringResource(Res.string.brief_spotlight_watchlist_desc)
                    )
                }
            }
        }
    }
}

private const val SHARE_STEP = 0
private const val WATCHLIST_STEP = 1
private const val STEP_COUNT = 2
private val CARD_TOP = GiSpace.x2 + GiSpace.touchTarget + GiSpace.x3
private val SHARE_CARET_END = 68.dp
private val STAR_CARET_END = 16.dp
