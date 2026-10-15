package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

@Composable
internal fun GiPreviewSurface(content: @Composable () -> Unit) {
    GPTInvestorTheme {
        Box(modifier = Modifier.background(GiTheme.colors.bg).padding(GiSpace.x4)) {
            content()
        }
    }
}
