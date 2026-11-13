package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun GiImage(
    url: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
    shape: Shape = RectangleShape,
    fallback: @Composable () -> Unit = {}
) {
    GiModelImage(
        model = url?.takeIf { it.isNotBlank() },
        contentDescription = contentDescription,
        contentScale = contentScale,
        shape = shape,
        modifier = modifier,
        fallback = fallback
    )
}

@Composable
fun GiImage(
    bytes: ByteArray?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
    shape: Shape = RectangleShape,
    fallback: @Composable () -> Unit = {}
) {
    GiModelImage(
        model = bytes,
        contentDescription = contentDescription,
        contentScale = contentScale,
        shape = shape,
        modifier = modifier,
        fallback = fallback
    )
}

@Composable
private fun GiModelImage(
    model: Any?,
    contentDescription: String?,
    contentScale: ContentScale,
    shape: Shape,
    modifier: Modifier = Modifier,
    fallback: @Composable () -> Unit
) {
    var isLoaded by remember(model) { mutableStateOf(false) }
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (!isLoaded) fallback()
        if (model != null) {
            AsyncImage(
                model = model,
                contentDescription = contentDescription,
                modifier = Modifier.matchParentSize().clip(shape),
                contentScale = contentScale,
                onState = { isLoaded = it is AsyncImagePainter.State.Success }
            )
        }
    }
}

@Composable
fun GiImage(
    resource: DrawableResource,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
    shape: Shape = RectangleShape
) {
    Image(
        painter = painterResource(resource),
        contentDescription = contentDescription,
        modifier = modifier.clip(shape),
        contentScale = contentScale
    )
}

@PreviewLightDark
@Composable
private fun GiImagePreview() {
    GiPreviewSurface {
        Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
            GiImage(url = null, modifier = Modifier.size(32.dp), shape = GiRadius.sm) {
                GiText(text = "NG", style = GiTextStyle.TileInitials)
            }
            GiImage(url = null, modifier = Modifier.size(16.dp), contentScale = ContentScale.Fit) {
                GiText(text = "US", style = GiTextStyle.Caption)
            }
        }
    }
}
