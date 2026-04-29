package com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified
import com.thejawnpaul.gptinvestor.features.component.ShimmerBox
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme

@Composable
fun CompanyBriefSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BriefHeaderSkeleton()
        BriefSentimentSkeleton()
        BriefSummarySkeleton()
        BriefKeyNumbersSkeleton()
        BriefNewsSkeleton()
        BriefRiskOpportunitySkeleton()
    }
}

@Composable
private fun BriefHeaderSkeleton(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ShimmerBox(size = 48.dp, shape = CircleShape)
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SkeletonLine(width = 56.dp, height = 18.dp)
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                SkeletonLine(width = 180.dp, height = 24.dp)
            }
        }
    }
}

@Composable
private fun BriefSentimentSkeleton(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SkeletonLine(
            height = 60.dp,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun BriefSummarySkeleton(modifier: Modifier = Modifier) {
    SkeletonSection(modifier = modifier, spacing = 8.dp) {
        SkeletonLine()
        SkeletonLine()
        SkeletonLine(width = 200.dp)
    }
}

@Composable
private fun BriefKeyNumbersSkeleton(modifier: Modifier = Modifier) {
    SkeletonSection(modifier = modifier, spacing = 0.dp) {
        repeat(3) { index ->
            KeyNumberItemSkeleton()
            if (index != 2) {
                SkeletonDivider()
            }
        }
    }
}

@Composable
private fun KeyNumberItemSkeleton(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(0.3f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            SkeletonLine(width = 48.dp, height = 12.dp)
            SkeletonLine(width = 64.dp, height = 24.dp)
        }
        SkeletonLine(modifier = Modifier.weight(0.7f), height = 24.dp, shape = RoundedCornerShape(16.dp))
    }
}

@Composable
private fun BriefNewsSkeleton(modifier: Modifier = Modifier) {
    SkeletonSection(modifier = modifier, spacing = 0.dp) {
        repeat(2) { index ->
            NewsItemSkeleton()
            if (index != 1) {
                SkeletonDivider()
            }
        }
    }
}

@Composable
private fun NewsItemSkeleton(modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SkeletonLine(width = 120.dp, height = 24.dp)
        SkeletonLine(height = 40.dp)
    }
}

@Composable
private fun BriefRiskOpportunitySkeleton(modifier: Modifier = Modifier) {
    SkeletonSection(modifier = modifier, spacing = 0.dp) {
        repeat(2) { index ->
            RiskOpportunityItemSkeleton()
            if (index != 1) {
                SkeletonDivider()
            }
        }
    }
}

@Composable
private fun RiskOpportunityItemSkeleton(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ShimmerBox(size = 32.dp, shape = RoundedCornerShape(8.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SkeletonLine(width = 80.dp, height = 16.dp)
            SkeletonLine()
            SkeletonLine(width = 180.dp)
        }
    }
}

@Composable
private fun SkeletonLine(
    modifier: Modifier = Modifier,
    width: Dp = Dp.Unspecified,
    height: Dp = 14.dp,
    shape: Shape = RoundedCornerShape(4.dp)
) {
    ShimmerBox(
        modifier = if (width.isUnspecified) modifier.fillMaxWidth() else modifier,
        width = width,
        height = height,
        shape = shape
    )
}

@Composable
private fun SkeletonSection(
    modifier: Modifier = Modifier,
    spacing: Dp = 12.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    BriefCard(modifier = modifier) {
        SkeletonLine(width = 100.dp, height = 14.dp, modifier = Modifier.padding(bottom = 12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(spacing)) {
            content()
        }
    }
}

@Composable
private fun SkeletonDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 12.dp),
        color = briefCardBorderColor()
    )
}

@PreviewLightDark
@Composable
private fun CompanyBriefSkeletonPreview() {
    GPTInvestorTheme {
        Surface {
            CompanyBriefSkeleton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}
