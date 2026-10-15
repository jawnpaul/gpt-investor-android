package com.thejawnpaul.gptinvestor.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle

// Legacy styles kept for screens not yet rebuilt in v4; each maps to its v4 equivalent.
@Suppress("UnusedReceiverParameter")
val Typography.bodyChatBody: TextStyle
    @Composable get() = GiTheme.type.body

@Suppress("UnusedReceiverParameter")
val Typography.linkMedium: TextStyle
    @Composable get() = GiTheme.type.link
