package com.thejawnpaul.gptinvestor.features.company.presentation.ui

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.back
import com.thejawnpaul.gptinvestor.core.platform.WebViewHost
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiTopBar
import org.jetbrains.compose.resources.stringResource
import org.koin.core.annotation.Singleton

@Singleton(binds = [WebViewHost::class])
@SuppressLint("SetJavaScriptEnabled")
class AndroidWebViewHost : WebViewHost {
    @Composable
    override fun WebView(url: String, onGoBack: () -> Unit, modifier: Modifier) {
        Scaffold(
            modifier = modifier,
            containerColor = GiTheme.colors.bg,
            contentWindowInsets = WindowInsets(0),
            topBar = {
                GiTopBar(
                    title = null,
                    onBack = onGoBack,
                    backContentDescription = stringResource(Res.string.back)
                )
            }
        ) { innerPadding ->
            AndroidView(
                modifier = Modifier.padding(innerPadding),
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        webViewClient = WebViewClient()

                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        settings.setSupportZoom(true)
                    }
                },

                update = { webView ->
                    webView.loadUrl(url)
                }
            )
        }
    }
}
