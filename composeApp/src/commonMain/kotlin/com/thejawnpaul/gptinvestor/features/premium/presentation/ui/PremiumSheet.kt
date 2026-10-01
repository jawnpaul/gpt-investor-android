package com.thejawnpaul.gptinvestor.features.premium.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.features.premium.presentation.state.PremiumSheetState
import com.thejawnpaul.gptinvestor.premium_billed_info
import com.thejawnpaul.gptinvestor.premium_feature_coverage_desc
import com.thejawnpaul.gptinvestor.premium_feature_coverage_title
import com.thejawnpaul.gptinvestor.premium_feature_digest_desc
import com.thejawnpaul.gptinvestor.premium_feature_digest_title
import com.thejawnpaul.gptinvestor.premium_feature_queries_desc
import com.thejawnpaul.gptinvestor.premium_feature_queries_title
import com.thejawnpaul.gptinvestor.premium_feature_research_desc
import com.thejawnpaul.gptinvestor.premium_feature_research_title
import com.thejawnpaul.gptinvestor.premium_sheet_subtitle
import com.thejawnpaul.gptinvestor.premium_sheet_title
import com.thejawnpaul.gptinvestor.subscribe_now
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumSheet(
    state: PremiumSheetState,
    onDismiss: () -> Unit,
    onPurchase: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        PremiumSheetContent(state = state, onPurchase = onPurchase)
    }
}

@Composable
private fun PremiumSheetContent(state: PremiumSheetState, onPurchase: () -> Unit) {
    val customColors = LocalGPTInvestorColors.current
    val accentColor = customColors.accentColors.allAccent

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .navigationBarsPadding()
            .padding(bottom = 24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(accentColor)
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = stringResource(Res.string.premium_sheet_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(Res.string.premium_sheet_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = customColors.textColors.secondary50
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        PremiumFeatureRow(
            icon = Icons.Filled.DateRange,
            title = stringResource(Res.string.premium_feature_digest_title),
            description = stringResource(Res.string.premium_feature_digest_desc)
        )
        Spacer(Modifier.height(16.dp))
        PremiumFeatureRow(
            icon = Icons.Filled.AllInclusive,
            title = stringResource(Res.string.premium_feature_queries_title),
            description = stringResource(Res.string.premium_feature_queries_desc)
        )
        Spacer(Modifier.height(16.dp))
        PremiumFeatureRow(
            icon = Icons.Filled.Unarchive,
            title = stringResource(Res.string.premium_feature_coverage_title),
            description = stringResource(Res.string.premium_feature_coverage_desc)
        )
        Spacer(Modifier.height(16.dp))
        PremiumFeatureRow(
            icon = Icons.Filled.Star,
            title = stringResource(Res.string.premium_feature_research_title),
            description = stringResource(Res.string.premium_feature_research_desc)
        )

        Spacer(Modifier.height(28.dp))

        Button(
            onClick = onPurchase,
            enabled = !state.isPurchasing,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            if (state.isPurchasing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = stringResource(Res.string.subscribe_now),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(Res.string.premium_billed_info),
            style = MaterialTheme.typography.bodySmall,
            color = customColors.textColors.secondary50,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PremiumFeatureRow(icon: ImageVector, title: String, description: String, modifier: Modifier = Modifier) {
    val color = LocalGPTInvestorColors.current.utilColors.borderBright10

    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color)

        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = LocalGPTInvestorColors.current.textColors.secondary50
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PremiumSheetPreview() {
    GPTInvestorTheme {
        Surface {
            PremiumSheetContent(
                state = PremiumSheetState(),
                onPurchase = {}
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PremiumSheetPurchasingPreview() {
    GPTInvestorTheme {
        Surface {
            PremiumSheetContent(
                state = PremiumSheetState(isPurchasing = true),
                onPurchase = {}
            )
        }
    }
}
