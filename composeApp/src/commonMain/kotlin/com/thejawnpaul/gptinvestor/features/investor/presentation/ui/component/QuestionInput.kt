package com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.send
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.components.GiAskComposer
import org.jetbrains.compose.resources.stringResource

// Legacy entry point kept for existing callers; draws the v4 Ask composer.
@Composable
fun QuestionInput(
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "",
    hint: String = "",
    onTextChange: (String) -> Unit = {}
) {
    GiAskComposer(
        value = text,
        onValueChange = onTextChange,
        onSend = onSendClick,
        placeholder = hint,
        sendContentDescription = stringResource(Res.string.send),
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = GiSpace.gutter, end = GiSpace.gutter, top = GiSpace.x3, bottom = GiSpace.x4)
    )
}

@PreviewLightDark
@Composable
private fun QuestionInputPreview() {
    GPTInvestorTheme {
        QuestionInput(
            onSendClick = {},
            text = "Why is NVDA up today?",
            hint = "Ask a follow-up question…"
        )
    }
}
