package com.thejawnpaul.gptinvestor.features.company.presentation.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownTypography
import com.mikepenz.markdown.model.rememberMarkdownState
import com.thejawnpaul.gptinvestor.theme.bodyChatBody

@Composable
fun CustomRichText(text: String, modifier: Modifier = Modifier, bodyTextStyle: TextStyle? = null) {
    val resolvedStyle = bodyTextStyle
        ?: MaterialTheme.typography.bodyChatBody.copy(color = MaterialTheme.colorScheme.onBackground)
    val markdownState = rememberMarkdownState(
        content = text,
        retainState = true
    )
    Markdown(
        markdownState = markdownState,
        modifier = modifier,
        typography = markdownTypography(
            paragraph = resolvedStyle,
            text = resolvedStyle,
            bullet = resolvedStyle
        )
    )
}
