package com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.model.TidbitPresentation
import com.thejawnpaul.gptinvestor.helpful
import com.thejawnpaul.gptinvestor.meta_pair
import com.thejawnpaul.gptinvestor.remove_from_saved
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiCard
import com.thejawnpaul.gptinvestor.theme.components.GiIconButton
import com.thejawnpaul.gptinvestor.theme.components.GiIconButtonVariant
import com.thejawnpaul.gptinvestor.theme.components.GiIconToggleButton
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle
import com.thejawnpaul.gptinvestor.tidbit_cover_label
import com.thejawnpaul.gptinvestor.tidbit_save
import com.thejawnpaul.gptinvestor.tidbit_share
import com.thejawnpaul.gptinvestor.tidbit_type_article
import com.thejawnpaul.gptinvestor.tidbit_type_audio
import com.thejawnpaul.gptinvestor.tidbit_type_video
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SingleTidbitItem(
    tidbit: TidbitPresentation,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onLikeClick: (String, Boolean) -> Unit = { _, _ -> },
    onSaveClick: (String, Boolean) -> Unit = { _, _ -> },
    onShareClick: (String) -> Unit = {},
    showPersonalActions: Boolean = true
) {
    var isLiked by remember(tidbit.id, tidbit.isLiked) { mutableStateOf(tidbit.isLiked) }
    var isBookmarked by remember(tidbit.id, tidbit.isBookmarked) { mutableStateOf(tidbit.isBookmarked) }
    val colors = GiTheme.colors

    GiCard(
        modifier = modifier.fillMaxWidth(),
        onClick = { onItemClick(tidbit.id) },
        spacing = GiSpace.x3
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
            GiText(
                text = tidbit.title,
                modifier = Modifier.semantics { heading() },
                style = GiTextStyle.SectionTitle
            )
            if (tidbit.summary.isNotBlank()) {
                GiText(
                    text = tidbit.summary,
                    style = GiTextStyle.BodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
            TidbitCover(
                title = tidbit.title,
                imageUrl = tidbit.previewUrl,
                modifier = Modifier
                    .padding(top = GiSpace.x1)
                    .fillMaxWidth()
                    .height(CARD_COVER_HEIGHT),
                alternate = tidbit.id.hashCode() % 2 != 0
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x2),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val meta = listOf(tidbit.originalAuthor, tidbit.category).filter { it.isNotBlank() }
            GiText(
                text = if (meta.size == 2) {
                    stringResource(Res.string.meta_pair, meta[0], meta[1])
                } else {
                    meta.firstOrNull().orEmpty()
                },
                modifier = Modifier.weight(1f),
                style = GiTextStyle.Meta,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            val type = tidbit.contentType()
            ContentTag(
                text = stringResource(type.label),
                container = colors.tile,
                content = colors.body,
                icon = type.icon,
                compact = true
            )
        }

        Row(
            modifier = Modifier.overlapMargin(start = GiSpace.x3, top = GiSpace.x1, bottom = GiSpace.x2),
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x1)
        ) {
            if (showPersonalActions) {
                GiIconToggleButton(
                    checked = isLiked,
                    onCheckedChange = {
                        onLikeClick(tidbit.id, it)
                        isLiked = it
                    },
                    icon = GiIcons.thumbsUp,
                    checkedIcon = GiIcons.thumbsUpFill,
                    contentDescription = stringResource(Res.string.helpful),
                    variant = GiIconButtonVariant.Ghost
                )
                GiIconToggleButton(
                    checked = isBookmarked,
                    onCheckedChange = {
                        onSaveClick(tidbit.id, it)
                        isBookmarked = it
                    },
                    icon = GiIcons.bookmarkSimple,
                    checkedIcon = ContentBookmarkFill,
                    contentDescription = stringResource(
                        if (isBookmarked) Res.string.remove_from_saved else Res.string.tidbit_save
                    ),
                    variant = GiIconButtonVariant.Ghost
                )
            }
            GiIconButton(
                icon = GiIcons.shareNetwork,
                contentDescription = stringResource(Res.string.tidbit_share),
                onClick = { onShareClick(tidbit.id) },
                variant = GiIconButtonVariant.Ghost
            )
        }
    }
}

@Composable
internal fun TidbitCover(
    title: String,
    imageUrl: String,
    modifier: Modifier = Modifier,
    alternate: Boolean = false,
    hero: Boolean = false
) {
    val colors = GiTheme.colors
    Box(
        modifier = modifier
            .clip(if (hero) GiRadius.md else GiRadius.sm)
            .background(if (alternate) colors.tile else colors.violetSoft)
            .clearAndSetSemantics { }
    ) {
        if (hero) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(GiSpace.x6),
                verticalArrangement = Arrangement.spacedBy(GiSpace.x3, Alignment.CenterVertically)
            ) {
                CoverMark()
                GiText(text = title, style = GiTextStyle.StatNumber, maxLines = 3, overflow = TextOverflow.Ellipsis)
                GiText(text = stringResource(Res.string.tidbit_cover_label), style = GiTextStyle.Eyebrow)
            }
        } else {
            CoverMark(modifier = Modifier.padding(GiSpace.x4))
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(GiSpace.x4),
                verticalArrangement = Arrangement.spacedBy(GiSpace.x2)
            ) {
                GiText(text = title, style = GiTextStyle.StatNumber, maxLines = 2, overflow = TextOverflow.Ellipsis)
                GiText(text = stringResource(Res.string.tidbit_cover_label), style = GiTextStyle.Caption)
            }
        }
        if (imageUrl.isNotBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun CoverMark(modifier: Modifier = Modifier, size: Dp = GiIconSize.lg) {
    Icon(
        imageVector = GiIcons.mark,
        contentDescription = null,
        modifier = modifier.size(size),
        tint = GiTheme.colors.ink
    )
}

@Composable
internal fun TidbitThumbnail(imageUrl: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(THUMB_SIZE)
            .clip(GiRadius.sm)
            .background(GiTheme.colors.violetSoft)
            .clearAndSetSemantics { }
    ) {
        CoverMark(modifier = Modifier.padding(GiSpace.x2_5), size = GiIconSize.sm)
        if (imageUrl.isNotBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

private val CARD_COVER_HEIGHT = 168.dp
private val THUMB_SIZE = 64.dp

class TidbitPreviewParameterProvider : PreviewParameterProvider<TidbitPresentation> {
    override val values = sequenceOf(
        TidbitPresentation.ArticlePresentation(
            id = "article1",
            name = "Rethink early retirement",
            title = "Rethink early retirement: lessons from the FIRE movement",
            summary =
            "Is retiring early your goal, or just an escape? See what the FIRE movement gets right and why a " +
                "middle path might suit you better.",
            originalAuthor = "Aaron Tang",
            category = "Personal growth",
            isBookmarked = true,
            isLiked = true
        ),
        TidbitPresentation.VideoPresentation(
            id = "video1",
            name = "Dangote IPO",
            title = "How to evaluate the Dangote Refinery IPO",
            summary = "The Dangote Refinery IPO is here. Learn how to look past the hype, check the valuation and " +
                "decide if it fits your portfolio.",
            originalAuthor = "GPT Investor",
            category = "Markets",
            isBookmarked = true
        )
    )
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun SingleTidbitItemPreview(
    @PreviewParameter(TidbitPreviewParameterProvider::class) tidbit: TidbitPresentation
) {
    GPTInvestorTheme {
        Box(modifier = Modifier.background(GiTheme.colors.bg).padding(GiSpace.gutter)) {
            SingleTidbitItem(tidbit = tidbit, onItemClick = {})
        }
    }
}

private class TidbitContentType(val icon: ImageVector, val label: StringResource)

private fun TidbitPresentation.contentType(): TidbitContentType = when (this) {
    is TidbitPresentation.ArticlePresentation ->
        TidbitContentType(Icons.AutoMirrored.Outlined.Article, Res.string.tidbit_type_article)
    is TidbitPresentation.VideoPresentation -> TidbitContentType(
        Icons.Outlined.PlayCircle,
        Res.string.tidbit_type_video
    )
    is TidbitPresentation.AudioPresentation -> TidbitContentType(
        Icons.Outlined.Headphones,
        Res.string.tidbit_type_audio
    )
}
