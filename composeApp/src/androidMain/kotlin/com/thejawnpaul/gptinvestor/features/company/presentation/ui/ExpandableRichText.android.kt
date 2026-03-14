package com.thejawnpaul.gptinvestor.features.company.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.halilibo.richtext.commonmark.Markdown
import com.halilibo.richtext.ui.BasicRichText
import com.halilibo.richtext.ui.RichTextThemeProvider
import com.thejawnpaul.gptinvestor.theme.bodyChatBody

@Composable
actual fun ExpandableRichText(text: String, modifier: Modifier, textModifier: Modifier, bodyTextStyle: TextStyle?) {
    // State variables to track the expanded state, clickable state.
    var isExpanded by remember { mutableStateOf(false) }
    val clickable by remember { mutableStateOf(false) }

    // Box composable containing the RichText composable.
    Box(
        modifier = Modifier
            .clickable(
                indication = null,
                enabled = clickable,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                // isExpanded = !isExpanded
            }
            .then(modifier)
    ) {
        RichTextThemeProvider(
            textStyleProvider = {
                bodyTextStyle
                    ?: MaterialTheme.typography.bodyChatBody.copy(
                        color = MaterialTheme.colorScheme.onBackground
                    )
            }
        ) {
            BasicRichText {
                val textToShow = if (isExpanded) {
                    text
                } else {
                    if (text.length < 250) {
                        text
                    } else {
                        text.substring(startIndex = 0, endIndex = text.length / 6)
                            .dropLastWhile { (it.isWhitespace() || it == '.') }
                    }
                }
                Markdown(content = textToShow)

                if (text.length > 250) {
                    TextButton(
                        modifier = Modifier
                            .align(Alignment.BottomEnd),
                        onClick = {
                            isExpanded = !isExpanded
                        }
                    ) {
                        if (isExpanded) {
                            Text(text = "Show less")
                        } else {
                            Text(text = "Show more")
                        }
                    }
                }
            }
        }
    }
}

@Composable
actual fun CustomRichText(text: String, modifier: Modifier, bodyTextStyle: TextStyle) {
    RichTextThemeProvider(
        textStyleProvider = {
            bodyTextStyle.copy(color = MaterialTheme.colorScheme.onBackground)
        }
    ) {
        BasicRichText(
            modifier = modifier
        ) {
            Markdown(content = text)
        }
    }
}
