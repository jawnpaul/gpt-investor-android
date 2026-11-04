package com.thejawnpaul.gptinvestor.features.news.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.close
import com.thejawnpaul.gptinvestor.features.news.presentation.SharePreviewUi
import com.thejawnpaul.gptinvestor.news_share_preview_description
import com.thejawnpaul.gptinvestor.news_share_title
import com.thejawnpaul.gptinvestor.share
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiBottomSheet
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SharePreviewSheet(preview: SharePreviewUi, onShare: () -> Unit, onDismiss: () -> Unit) {
    GiBottomSheet(
        onDismissRequest = onDismiss,
        title = stringResource(Res.string.news_share_title),
        closeContentDescription = stringResource(Res.string.close)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x4)) {
            AsyncImage(
                model = preview.imageUrl,
                contentDescription = stringResource(Res.string.news_share_preview_description),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 5f)
                    .clip(GiRadius.md)
                    .background(GiTheme.colors.tile),
                contentScale = ContentScale.Crop
            )
            GiButton(
                text = stringResource(Res.string.share),
                onClick = onShare,
                leadingIcon = GiIcons.shareNetwork,
                loading = preview.preparing,
                enabled = !preview.preparing,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
