package com.thejawnpaul.gptinvestor.core.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface WebViewHost {
    @Composable
    fun WebView(url: String, onGoBack: () -> Unit, modifier: Modifier)
}
