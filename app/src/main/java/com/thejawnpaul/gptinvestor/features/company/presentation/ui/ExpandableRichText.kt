package com.thejawnpaul.gptinvestor.features.company.presentation.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.halilibo.richtext.commonmark.Markdown
import com.halilibo.richtext.ui.material3.RichText

const val DEFAULT_MINIMUM_TEXT_LINEE = 10

/**
 * An expandable text component that provides access to truncated text with a dynamic ... Show More/Show Less button.
 *
 * @param modifier Modifier for the Box containing the text.
 * @param textModifier Modifier for the Text composable.
 * @param text The text to be displayed.
 */
@Composable
fun ExpandableRichText(modifier: Modifier = Modifier, textModifier: Modifier = Modifier, text: String) {
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
        RichText(
            modifier = textModifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            val textToShow = if (isExpanded) {
                text
            } else {
                text.substring(startIndex = 0, endIndex = text.length.div(6))
                    .dropLastWhile { Character.isWhitespace(it) || it == '.' }
            }

            Markdown(content = textToShow)

            OutlinedButton(onClick = {
                isExpanded = !isExpanded
            }, modifier = Modifier.align(Alignment.BottomEnd)) {
                if (isExpanded) {
                    Text(text = "Show less")
                } else {
                    Text(text = "Show more")
                }
            }
        }
    }
}
