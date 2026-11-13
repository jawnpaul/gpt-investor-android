package com.thejawnpaul.gptinvestor.features.news.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.close
import com.thejawnpaul.gptinvestor.features.news.presentation.SharePreviewUi
import com.thejawnpaul.gptinvestor.news_share_preview_description
import com.thejawnpaul.gptinvestor.news_share_title
import com.thejawnpaul.gptinvestor.share
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.components.GiBottomSheet
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiImage
import com.thejawnpaul.gptinvestor.theme.components.GiSkeleton
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SharePreviewSheet(preview: SharePreviewUi, onShare: () -> Unit, onDismiss: () -> Unit) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
    )
    var leaving by remember { mutableStateOf(false) }
    GiBottomSheet(
        onDismissRequest = onDismiss,
        title = stringResource(Res.string.news_share_title),
        closeContentDescription = stringResource(Res.string.close),
        sheetState = sheetState
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x4)) {
            val imageModifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 5f)
            GiImage(
                bytes = preview.image,
                modifier = imageModifier,
                contentDescription = stringResource(Res.string.news_share_preview_description),
                shape = GiRadius.md
            ) {
                GiSkeleton(modifier = Modifier.fillMaxSize(), shape = GiRadius.md)
            }
            GiButton(
                text = stringResource(Res.string.share),
                onClick = {
                    if (!leaving) {
                        leaving = true
                        scope.launch { sheetState.hide() }.invokeOnCompletion { onShare() }
                    }
                },
                leadingIcon = GiIcons.shareNetwork,
                loading = !preview.ready,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
