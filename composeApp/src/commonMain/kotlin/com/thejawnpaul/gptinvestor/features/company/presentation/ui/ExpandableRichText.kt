package com.thejawnpaul.gptinvestor.features.company.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownTypography
import com.mikepenz.markdown.model.rememberMarkdownState
import com.thejawnpaul.gptinvestor.theme.bodyChatBody

@Composable
fun ExpandableRichText(text: String, modifier: Modifier = Modifier, bodyTextStyle: TextStyle? = null) {
    var isExpanded by remember { mutableStateOf(false) }
    val clickable by remember { mutableStateOf(false) }
    var buttonHeightPx by remember { mutableIntStateOf(0) }
    val buttonHeightDp = with(LocalDensity.current) { buttonHeightPx.toDp() }

    Box(
        modifier = Modifier
            .clickable(
                indication = null,
                enabled = clickable,
                onClick = {},
                interactionSource = remember { MutableInteractionSource() }
            )
            .then(modifier)
    ) {
        val textToShow = if (isExpanded) {
            text
        } else {
            if (text.length < 250) {
                text
            } else {
                text.substring(0, text.length / 6)
                    .dropLastWhile { it.isWhitespace() || it == '.' }
            }
        }

        val markdownState = rememberMarkdownState(
            content = textToShow,
            retainState = true
        )

        val resolvedStyle = bodyTextStyle
            ?: MaterialTheme.typography.bodyChatBody.copy(color = MaterialTheme.colorScheme.onBackground)

        Markdown(
            markdownState = markdownState,
            modifier = Modifier.padding(bottom = if (text.length > 250) buttonHeightDp else 0.dp),
            typography = markdownTypography(
                paragraph = resolvedStyle,
                text = resolvedStyle,
                bullet = resolvedStyle
            )
        )

        if (text.length > 250) {
            TextButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .onSizeChanged { buttonHeightPx = it.height },
                onClick = { isExpanded = !isExpanded }
            ) {
                Text(text = if (isExpanded) "Show less" else "Show more")
            }
        }
    }
}

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
