package com.thejawnpaul.gptinvestor.core.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.core.annotation.Singleton

@Singleton(binds = [WebViewHost::class])
class IosWebViewHost : WebViewHost {
    @Composable
    override fun WebView(url: String, onGoBack: () -> Unit, modifier: Modifier) {
        // Simple stub for iOS v1
    }
}
