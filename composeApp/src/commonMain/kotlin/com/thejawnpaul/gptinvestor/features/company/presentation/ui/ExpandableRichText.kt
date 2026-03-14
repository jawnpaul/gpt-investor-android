package com.thejawnpaul.gptinvestor.features.company.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

/**
 * An expandable text component that provides access to truncated text with a dynamic ... Show More/Show Less button.
 *
 * @param modifier Modifier for the Box containing the text.
 * @param textModifier Modifier for the Text composable.
 * @param text The text to be displayed.
 * @param bodyTextStyle The TextStyle for the text body.
 */
@Composable
expect fun ExpandableRichText(
    text: String,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    bodyTextStyle: TextStyle? = null
)

@Composable
expect fun CustomRichText(text: String, modifier: Modifier = Modifier, bodyTextStyle: TextStyle = TextStyle.Default)
