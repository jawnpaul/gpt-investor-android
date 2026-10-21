package com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography
import com.mikepenz.markdown.model.markdownPadding
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.back
import com.thejawnpaul.gptinvestor.check_connection_try_again
import com.thejawnpaul.gptinvestor.features.guest.presentation.TopGuestLabel
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.model.TidbitPresentation
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.viewmodel.TidbitDetailEvent
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.viewmodel.TidbitDetailState
import com.thejawnpaul.gptinvestor.helpful
import com.thejawnpaul.gptinvestor.meta_pair
import com.thejawnpaul.gptinvestor.remove_from_saved
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiButtonSize
import com.thejawnpaul.gptinvestor.theme.components.GiButtonVariant
import com.thejawnpaul.gptinvestor.theme.components.GiErrorState
import com.thejawnpaul.gptinvestor.theme.components.GiIconButton
import com.thejawnpaul.gptinvestor.theme.components.GiIconToggleButton
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiReadingProgress
import com.thejawnpaul.gptinvestor.theme.components.GiSkeleton
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle
import com.thejawnpaul.gptinvestor.theme.components.GiToastHost
import com.thejawnpaul.gptinvestor.theme.components.GiToastResult
import com.thejawnpaul.gptinvestor.theme.components.rememberGiToastState
import com.thejawnpaul.gptinvestor.tidbit_error_title
import com.thejawnpaul.gptinvestor.tidbit_loading
import com.thejawnpaul.gptinvestor.tidbit_read_minutes
import com.thejawnpaul.gptinvestor.tidbit_read_source
import com.thejawnpaul.gptinvestor.tidbit_save
import com.thejawnpaul.gptinvestor.tidbit_saved_to_library
import com.thejawnpaul.gptinvestor.tidbit_share
import com.thejawnpaul.gptinvestor.tidbit_short_summary_body
import com.thejawnpaul.gptinvestor.tidbit_short_summary_title
import com.thejawnpaul.gptinvestor.try_again
import com.thejawnpaul.gptinvestor.view
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun TidbitDetailScreen(
    state: TidbitDetailState,
    onEvent: (TidbitDetailEvent) -> Unit,
    modifier: Modifier = Modifier,
    onOpenSavedList: (() -> Unit)? = null
) {
    val colors = GiTheme.colors
    val scrollState = rememberScrollState()
    val toastState = rememberGiToastState()
    val scope = rememberCoroutineScope()
    val savedMessage = stringResource(Res.string.tidbit_saved_to_library)
    val viewLabel = stringResource(Res.string.view)
    val onSave: () -> Unit = {
        scope.launch {
            val result = toastState.show(
                message = savedMessage,
                icon = GiIcons.check,
                actionText = if (onOpenSavedList != null) viewLabel else null
            )
            if (result == GiToastResult.ActionPerformed) onOpenSavedList?.invoke()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.raised)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .background(colors.raised)
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                GiReadingProgress(
                    progress = {
                        when {
                            state.presentation == null || state.isLoading -> 0f
                            scrollState.maxValue == 0 -> 1f
                            else -> scrollState.value.toFloat() / scrollState.maxValue
                        }
                    }
                )
                Row(
                    modifier = Modifier.padding(
                        start = GiSpace.gutter,
                        end = GiSpace.gutter,
                        top = GiSpace.x2,
                        bottom = GiSpace.x3
                    )
                ) {
                    GiIconButton(
                        icon = GiIcons.caretLeft,
                        contentDescription = stringResource(Res.string.back),
                        onClick = { onEvent(TidbitDetailEvent.GoBack) }
                    )
                }
                if (state.isGuestSession) {
                    TopGuestLabel(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = GiSpace.gutter, end = GiSpace.gutter, bottom = GiSpace.x2),
                        onClick = { onEvent(TidbitDetailEvent.GoToSignUp) }
                    )
                }
            }

            val presentation = state.presentation
            when {
                state.isLoading -> TidbitDetailSkeleton()
                presentation != null -> TidbitArticle(
                    presentation = presentation,
                    isGuest = state.isGuestSession,
                    scrollState = scrollState,
                    onEvent = onEvent,
                    onSave = onSave
                )
                else -> GiErrorState(
                    title = stringResource(Res.string.tidbit_error_title),
                    body = stringResource(Res.string.check_connection_try_again),
                    actionText = stringResource(Res.string.try_again),
                    onAction = { onEvent(TidbitDetailEvent.Retry) }
                )
            }
        }

        GiToastHost(
            state = toastState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = GiSpace.gutter, vertical = GiSpace.x4)
        )
    }
}

@Composable
private fun TidbitArticle(
    presentation: TidbitPresentation,
    isGuest: Boolean,
    scrollState: androidx.compose.foundation.ScrollState,
    onEvent: (TidbitDetailEvent) -> Unit,
    onSave: () -> Unit
) {
    val colors = GiTheme.colors
    val navBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    var isLiked by remember(presentation.id) { mutableStateOf(presentation.isLiked) }
    var isBookmarked by remember(presentation.id) { mutableStateOf(presentation.isBookmarked) }
    val sourceUrl = when (presentation) {
        is TidbitPresentation.ArticlePresentation -> presentation.sourceUrl
        is TidbitPresentation.VideoPresentation -> presentation.sourceUrl
        is TidbitPresentation.AudioPresentation -> presentation.sourceUrl
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(start = GiSpace.gutter, end = GiSpace.gutter, top = GiSpace.x2, bottom = GiSpace.x10 + navBottom),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x4)
    ) {
        TidbitMedia(presentation = presentation)

        Row(
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val minutes =
                stringResource(
                    Res.string.tidbit_read_minutes,
                    remember(presentation.content) {
                        readMinutes(presentation.content)
                    }
                )
            GiText(
                text = if (presentation.originalAuthor.isNotBlank()) {
                    stringResource(Res.string.meta_pair, presentation.originalAuthor, minutes)
                } else {
                    minutes
                },
                modifier = Modifier.weight(1f),
                style = GiTextStyle.MetaRelaxed
            )
            if (presentation.category.isNotBlank()) {
                ContentTag(text = presentation.category, container = colors.violetSoft, content = colors.violet)
            }
        }

        GiText(
            text = presentation.title,
            modifier = Modifier.semantics { heading() },
            style = GiTextStyle.ScreenTitle
        )

        ArticleBody(content = presentation.content)

        Spacer(modifier = Modifier.height(GiSpace.x2))

        if (sourceUrl.isNotBlank()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(GiRadius.sm)
                    .background(colors.tile)
                    .padding(GiSpace.x4),
                verticalArrangement = Arrangement.spacedBy(GiSpace.x2)
            ) {
                GiText(text = stringResource(Res.string.tidbit_short_summary_title), style = GiTextStyle.FieldLabel)
                GiText(text = stringResource(Res.string.tidbit_short_summary_body), style = GiTextStyle.BodySmall)
            }
        }

        val actions: @Composable () -> Unit = {
            Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
                if (!isGuest) {
                    GiIconToggleButton(
                        checked = isLiked,
                        onCheckedChange = {
                            onEvent(TidbitDetailEvent.OnClickLike(id = presentation.id, newValue = it))
                            isLiked = it
                        },
                        icon = GiIcons.thumbsUp,
                        checkedIcon = GiIcons.thumbsUpFill,
                        contentDescription = stringResource(Res.string.helpful)
                    )
                    GiIconToggleButton(
                        checked = isBookmarked,
                        onCheckedChange = {
                            onEvent(TidbitDetailEvent.OnClickBookmark(id = presentation.id, newValue = it))
                            isBookmarked = it
                            if (it) onSave()
                        },
                        icon = GiIcons.bookmarkSimple,
                        checkedIcon = ContentBookmarkFill,
                        contentDescription = stringResource(
                            if (isBookmarked) {
                                Res.string.remove_from_saved
                            } else {
                                Res.string.tidbit_save
                            }
                        )
                    )
                }
                GiIconButton(
                    icon = GiIcons.shareNetwork,
                    contentDescription = stringResource(Res.string.tidbit_share),
                    onClick = { onEvent(TidbitDetailEvent.OnClickShare) }
                )
            }
        }
        val readSource: @Composable (Modifier) -> Unit = { buttonModifier ->
            if (sourceUrl.isNotBlank()) {
                GiButton(
                    text = stringResource(Res.string.tidbit_read_source),
                    onClick = { onEvent(TidbitDetailEvent.OnClickSource) },
                    modifier = buttonModifier,
                    size = GiButtonSize.Medium,
                    variant = GiButtonVariant.Secondary,
                    leadingIcon = GiIcons.globe
                )
            }
        }
        if (isContentLargeText()) {
            Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
                actions()
                readSource(Modifier.fillMaxWidth())
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                actions()
                Spacer(modifier = Modifier.weight(1f))
                readSource(Modifier)
            }
        }
    }
}

@Composable
private fun TidbitMedia(presentation: TidbitPresentation) {
    val heroModifier = Modifier
        .fillMaxWidth()
        .heightIn(min = HERO_HEIGHT)
        .clip(GiRadius.md)
        .border(GiEdge.width, GiTheme.colors.line, GiRadius.md)
    when (presentation) {
        is TidbitPresentation.ArticlePresentation -> TidbitCover(
            title = presentation.title,
            imageUrl = presentation.mediaUrl.ifBlank { presentation.previewUrl },
            modifier = heroModifier.height(HERO_HEIGHT),
            hero = true
        )

        is TidbitPresentation.VideoPresentation -> Box(modifier = heroModifier) {
            GptInvestorVideo(
                modifier = Modifier.fillMaxSize(),
                youtubeVideoId = presentation.videoId,
                url = presentation.mediaUrl,
                autoplay = true,
                showControls = true
            )
        }

        is TidbitPresentation.AudioPresentation -> Box(modifier = heroModifier.height(HERO_HEIGHT)) {
            GptInvestorVideo(
                modifier = Modifier.fillMaxSize(),
                url = presentation.mediaUrl,
                autoplay = true,
                showControls = true
            )
        }
    }
}

@Composable
private fun ArticleBody(content: String) {
    val colors = GiTheme.colors
    val type = GiTheme.type
    val body = articleBodyStyle()
    val heading = type.sectionTitle.copy(color = colors.ink)
    val typography = markdownTypography(
        h1 = type.pageTitle.copy(color = colors.ink),
        h2 = type.companyName.copy(color = colors.ink),
        h3 = heading,
        h4 = heading,
        h5 = heading,
        h6 = heading,
        text = body,
        code = body,
        inlineCode = body,
        quote = body,
        paragraph = body,
        ordered = body,
        bullet = body,
        list = body,
        textLink = TextLinkStyles(style = SpanStyle(color = colors.violet, fontWeight = FontWeight.SemiBold)),
        table = body
    )
    val markdownColors = markdownColor(
        text = colors.body,
        codeBackground = colors.tile,
        inlineCodeBackground = colors.tile,
        dividerColor = colors.line,
        tableBackground = colors.tile
    )
    val blocks = remember(content) {
        content.split(PARAGRAPH_BREAK).map { it.trim() }.filter { it.isNotEmpty() }
    }
    Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x4)) {
        blocks.forEach { block ->
            Markdown(
                content = block,
                colors = markdownColors,
                typography = typography,
                modifier = Modifier.fillMaxWidth(),
                padding = markdownPadding(block = 0.dp)
            )
        }
    }
}

private val PARAGRAPH_BREAK = Regex("\\n\\s*\\n")

@Composable
private fun TidbitDetailSkeleton() {
    val loading = stringResource(Res.string.tidbit_loading)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = GiSpace.gutter, end = GiSpace.gutter, top = GiSpace.x2)
            .semantics {
                contentDescription = loading
                liveRegion = LiveRegionMode.Polite
            },
        verticalArrangement = Arrangement.spacedBy(GiSpace.x4)
    ) {
        GiSkeleton(modifier = Modifier.fillMaxWidth().height(HERO_HEIGHT), shape = GiRadius.md)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GiSkeleton(modifier = Modifier.width(SKELETON_BYLINE_WIDTH).height(SKELETON_LINE))
            GiSkeleton(modifier = Modifier.width(SKELETON_TAG_WIDTH).height(GiSpace.x6), shape = GiRadius.xs)
        }
        GiSkeleton(modifier = Modifier.fillMaxWidth().height(GiSpace.x7))
        GiSkeleton(modifier = Modifier.fillMaxWidth(SKELETON_TITLE_FRACTION).height(GiSpace.x7))
        Spacer(modifier = Modifier.height(GiSpace.x1))
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
            SKELETON_LINES.forEach { fraction ->
                GiSkeleton(modifier = Modifier.fillMaxWidth(fraction).height(SKELETON_LINE))
            }
        }
    }
}

private val HERO_HEIGHT = 200.dp
private val SKELETON_LINE = 14.dp
private val SKELETON_BYLINE_WIDTH = 140.dp
private val SKELETON_TAG_WIDTH = 104.dp
private const val SKELETON_TITLE_FRACTION = 0.7f
private val SKELETON_LINES = listOf(1f, 1f, 1f, 1f, 0.62f, 1f, 1f, 1f, 1f, 0.62f)

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun TidbitDetailScreenPreview() {
    GPTInvestorTheme {
        TidbitDetailScreen(
            onEvent = {},
            state = TidbitDetailState(
                id = "1",
                presentation = TidbitPresentation.ArticlePresentation(
                    id = "1",
                    name = "FIRE",
                    title = "Rethink early retirement: lessons from the FIRE movement",
                    content =
                    "The Financial Independence, Retire Early movement, known as FIRE, is built on the idea " +
                        "that you can quit the traditional workforce decades ahead of schedule.\n\n" +
                        "A more sustainable " +
                        "approach is to find the middle path.",
                    originalAuthor = "Aaron Tang",
                    category = "Personal growth",
                    sourceUrl = "https://example.com"
                )
            )
        )
    }
}

@PreviewLightDark
@Composable
private fun TidbitDetailLoadingPreview() {
    GPTInvestorTheme {
        TidbitDetailScreen(onEvent = {}, state = TidbitDetailState(isLoading = true))
    }
}
