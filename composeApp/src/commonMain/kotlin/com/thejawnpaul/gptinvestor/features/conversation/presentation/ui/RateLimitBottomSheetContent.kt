package com.thejawnpaul.gptinvestor.features.conversation.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.rate_limit_bottom_sheet_action
import com.thejawnpaul.gptinvestor.rate_limit_bottom_sheet_description
import com.thejawnpaul.gptinvestor.rate_limit_bottom_sheet_title
import com.thejawnpaul.gptinvestor.upgrade
import org.jetbrains.compose.resources.stringResource

@Composable
fun RateLimitBottomSheetContent(onUpgrade: () -> Unit, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.rate_limit_bottom_sheet_title),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(Res.string.rate_limit_bottom_sheet_description),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            onClick = onUpgrade
        ) {
            Text(text = stringResource(Res.string.upgrade))
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            onClick = onDismiss
        ) {
            Text(text = stringResource(Res.string.rate_limit_bottom_sheet_action))
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}
