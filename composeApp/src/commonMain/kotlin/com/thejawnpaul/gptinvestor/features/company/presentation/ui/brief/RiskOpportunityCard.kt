package com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.features.company.domain.model.BriefSection
import com.thejawnpaul.gptinvestor.section_risk_opportunity
import com.thejawnpaul.gptinvestor.the_opportunity
import com.thejawnpaul.gptinvestor.the_risk
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun RiskOpportunityCard(risk: BriefSection?, opportunity: BriefSection?, modifier: Modifier = Modifier) {
    BriefCard(modifier = modifier) {
        BriefSectionTitle(text = stringResource(Res.string.section_risk_opportunity))
        Spacer(Modifier.height(12.dp))

        risk?.let { section ->
            RiskOpportunityRow(
                title = section.title.ifBlank { stringResource(Res.string.the_risk) },
                body = section.body,
                icon = Icons.Outlined.WarningAmber
            )
        }

        if (risk != null && opportunity != null) {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = briefCardBorderColor()
            )
        }

        opportunity?.let { section ->
            RiskOpportunityRow(
                title = section.title.ifBlank { stringResource(Res.string.the_opportunity) },
                body = section.body,
                icon = Icons.Outlined.Check
            )
        }
    }
}

@Composable
private fun RiskOpportunityRow(title: String, body: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = briefCardBorderColor(),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(18.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(6.dp))
            BriefExpandableText(text = body)
        }
    }
}

@PreviewLightDark
@Composable
private fun RiskOpportunityCardPreview() {
    GPTInvestorTheme {
        Surface {
            RiskOpportunityCard(
                modifier = Modifier.padding(16.dp),
                risk = BriefSection(
                    title = "The Risk",
                    body = "Most revenue still comes from one product — the iPhone. A weak upgrade cycle in any " +
                        "year noticeably dents the entire business and weighs on the stock."
                ),
                opportunity = BriefSection(
                    title = "The Opportunity",
                    body = "Services now grow more than twice as fast as hardware, and have higher profit " +
                        "margins, which steadily lifts overall earnings power."
                )
            )
        }
    }
}
