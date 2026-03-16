package com.thejawnpaul.gptinvestor.features.company.presentation.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

@Composable
actual fun ExpandableRichText(text: String, modifier: Modifier, textModifier: Modifier, bodyTextStyle: TextStyle?) {
    // For iOS, until a markdown library is available, we use ExpandableText.
    ExpandableText(
        text = text,
        modifier = modifier,
        textModifier = textModifier,
        style = bodyTextStyle ?: TextStyle.Default
    )
}

@Composable
actual fun CustomRichText(text: String, modifier: Modifier, bodyTextStyle: TextStyle) {
    // For iOS, until a markdown library is available, we use standard Text.
    Text(
        text = text,
        modifier = modifier,
        style = bodyTextStyle
    )
}
