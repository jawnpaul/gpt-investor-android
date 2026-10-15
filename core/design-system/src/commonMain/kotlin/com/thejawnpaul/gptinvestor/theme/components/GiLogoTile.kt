package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

// Decorative: the company name always sits next to the tile, so screen readers skip it.
@Composable
fun GiLogoTile(initials: String, brandColor: Color, modifier: Modifier = Modifier, imageUrl: String? = null) {
    var isLogoLoaded by remember(imageUrl) { mutableStateOf(false) }
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(GiRadius.sm)
            .background(brandColor)
            .clearAndSetSemantics { },
        contentAlignment = Alignment.Center
    ) {
        if (!isLogoLoaded) {
            Text(
                text = initials,
                style = GiTheme.type.tag.copy(fontSize = 11.sp),
                color = Color.White,
                maxLines = 1
            )
        }
        if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                onState = { isLogoLoaded = it is AsyncImagePainter.State.Success }
            )
        }
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiLogoTilePreview() {
    GiPreviewSurface {
        Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
            GiLogoTile(initials = "NV", brandColor = Color(0xFF76B900))
            GiLogoTile(initials = "AA", brandColor = Color(0xFF0F121B))
            GiLogoTile(initials = "MS", brandColor = Color(0xFF00A4EF))
        }
    }
}
