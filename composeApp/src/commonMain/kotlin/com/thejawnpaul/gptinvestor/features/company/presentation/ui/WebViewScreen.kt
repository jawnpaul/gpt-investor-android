package com.thejawnpaul.gptinvestor.features.company.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.thejawnpaul.gptinvestor.core.platform.WebViewHost
import org.koin.compose.koinInject

@Composable
fun WebViewScreen(url: String, onGoBack: () -> Unit, modifier: Modifier = Modifier) {
    val webViewHost: WebViewHost = koinInject()
    webViewHost.WebView(url = url, onGoBack = onGoBack, modifier = modifier)
}
