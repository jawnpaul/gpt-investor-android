package com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.ask_about_this
import com.thejawnpaul.gptinvestor.close
import com.thejawnpaul.gptinvestor.features.company.domain.model.KeyNumber
import com.thejawnpaul.gptinvestor.features.company.domain.model.KeyNumberType
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiBottomSheet
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiButtonSize
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiKeyNumberHero
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyKeyNumberSheet(
    keyNumber: KeyNumber,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onAsk: (() -> Unit)? = null
) {
    val colors = GiTheme.colors
    val type = GiTheme.type
    val scope = rememberCoroutineScope()
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
    )
    GiBottomSheet(
        onDismissRequest = onDismiss,
        title = stringResource(keyNumber.key.labelRes()),
        closeContentDescription = stringResource(Res.string.close),
        modifier = modifier,
        sheetState = sheetState
    ) {
        GiKeyNumberHero(
            caption = stringResource(keyNumber.key.periodRes()),
            value = keyNumber.value,
            tone = keyNumber.tone.toGi(),
            showTrend = keyNumber.key == KeyNumberType.RevenueGrowth
        )
        keyNumber.insight?.let { Text(text = it, color = colors.body, style = type.body) }
        Text(text = stringResource(keyNumber.key.definitionRes()), color = colors.muted, style = type.bodySmall)
        if (onAsk != null) {
            GiButton(
                text = stringResource(Res.string.ask_about_this),
                onClick = { scope.launch { sheetState.hide() }.invokeOnCompletion { onAsk() } },
                modifier = Modifier.fillMaxWidth(),
                size = GiButtonSize.Large,
                leadingIcon = GiIcons.chatCircleDots
            )
        }
    }
}
