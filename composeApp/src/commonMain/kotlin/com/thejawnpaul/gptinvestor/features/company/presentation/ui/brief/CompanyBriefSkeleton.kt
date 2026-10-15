package com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.loading_brief
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiCard
import com.thejawnpaul.gptinvestor.theme.components.GiDivider
import com.thejawnpaul.gptinvestor.theme.components.GiListGroup
import com.thejawnpaul.gptinvestor.theme.components.GiSkeleton
import org.jetbrains.compose.resources.stringResource

@Composable
fun CompanyBriefSkeleton(
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit = { CompanyBriefHeaderSkeleton() }
) {
    val description = stringResource(Res.string.loading_brief)
    Column(
        modifier = modifier.clearAndSetSemantics {
            liveRegion = LiveRegionMode.Polite
            contentDescription = description
        },
        verticalArrangement = Arrangement.spacedBy(GiSpace.x4)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
            header()
            Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
                GiSkeleton(modifier = Modifier.size(width = 120.dp, height = 32.dp))
                GiSkeleton(modifier = Modifier.size(width = 96.dp, height = 24.dp), shape = GiRadius.xs)
            }
        }
        GiCard(modifier = Modifier.fillMaxWidth()) {
            GiSkeleton(modifier = Modifier.size(width = 72.dp, height = 24.dp), shape = GiRadius.xs)
            GiSkeleton(modifier = Modifier.fillMaxWidth().height(20.dp))
            GiSkeleton(modifier = Modifier.fillMaxWidth(0.7f).height(20.dp))
        }
        GiListGroup {
            Column(
                modifier = Modifier.padding(GiSpace.cardPad),
                verticalArrangement = Arrangement.spacedBy(GiSpace.x3)
            ) {
                GiSkeleton(modifier = Modifier.size(width = 96.dp, height = 20.dp))
                listOf(1f, 1f, 0.6f).forEach { width ->
                    GiSkeleton(modifier = Modifier.fillMaxWidth(width).height(14.dp))
                }
            }
            GiDivider()
            Column(
                modifier = Modifier.padding(GiSpace.cardPad),
                verticalArrangement = Arrangement.spacedBy(GiSpace.x3)
            ) {
                GiSkeleton(modifier = Modifier.size(width = 120.dp, height = 20.dp))
                repeat(3) { index ->
                    if (index > 0) GiDivider()
                    KeyNumberRowSkeleton()
                }
            }
        }
    }
}

@Composable
private fun CompanyBriefHeaderSkeleton(modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
        GiSkeleton(modifier = Modifier.size(40.dp))
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
            GiSkeleton(modifier = Modifier.size(width = 148.dp, height = 16.dp))
            GiSkeleton(modifier = Modifier.size(width = 80.dp, height = 12.dp))
        }
    }
}

@Composable
private fun KeyNumberRowSkeleton(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(vertical = GiSpace.x3),
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x3)
    ) {
        Column(modifier = Modifier.width(104.dp), verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
            GiSkeleton(modifier = Modifier.size(width = 64.dp, height = 12.dp))
            GiSkeleton(modifier = Modifier.size(width = 72.dp, height = 20.dp))
        }
        GiSkeleton(modifier = Modifier.weight(1f).height(40.dp))
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun CompanyBriefSkeletonPreview() {
    GPTInvestorTheme {
        CompanyBriefSkeleton(
            modifier = Modifier
                .background(GiTheme.colors.bg)
                .padding(GiSpace.gutter)
        )
    }
}
