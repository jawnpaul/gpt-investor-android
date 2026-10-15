package com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.close
import com.thejawnpaul.gptinvestor.features.company.domain.model.KeyNumber
import com.thejawnpaul.gptinvestor.features.company.domain.model.KeyNumberType
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiBottomSheet
import com.thejawnpaul.gptinvestor.theme.components.GiKeyNumberHero
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyKeyNumberSheet(keyNumber: KeyNumber, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    val colors = GiTheme.colors
    val type = GiTheme.type
    GiBottomSheet(
        onDismissRequest = onDismiss,
        title = stringResource(keyNumber.key.labelRes()),
        closeContentDescription = stringResource(Res.string.close),
        modifier = modifier
    ) {
        GiKeyNumberHero(
            caption = stringResource(keyNumber.key.periodRes()),
            value = keyNumber.value,
            tone = keyNumber.tone.toGi(),
            showTrend = keyNumber.key == KeyNumberType.RevenueGrowth
        )
        keyNumber.insight?.let { Text(text = it, color = colors.body, style = type.body) }
        Text(text = stringResource(keyNumber.key.definitionRes()), color = colors.muted, style = type.bodySmall)
    }
}
