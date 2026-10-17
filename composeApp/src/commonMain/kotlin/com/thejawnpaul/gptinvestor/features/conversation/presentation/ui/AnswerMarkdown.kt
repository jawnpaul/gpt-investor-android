package com.thejawnpaul.gptinvestor.features.conversation.presentation.ui

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.mikepenz.markdown.annotator.AnnotatorSettings
import com.mikepenz.markdown.annotator.DefaultAnnotatorSettings
import com.mikepenz.markdown.annotator.buildMarkdownAnnotatedString
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.elements.MarkdownCheckBox
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography
import com.mikepenz.markdown.model.markdownAnnotator
import com.mikepenz.markdown.model.markdownInlineContent
import com.mikepenz.markdown.model.markdownPadding
import com.mikepenz.markdown.model.rememberMarkdownState
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import org.intellij.markdown.MarkdownElementTypes

@Composable
internal fun AnswerMarkdown(text: String, modifier: Modifier = Modifier, tail: AnswerTail? = null) {
    val colors = GiTheme.colors
    val type = GiTheme.type
    val markdownState = rememberMarkdownState(content = text, retainState = true, immediate = true)
    val links = remember(colors.violet) { TextLinkStyles(SpanStyle(color = colors.violet)) }
    val heading = remember(type.sectionTitle, colors.ink) { type.sectionTitle.copy(color = colors.ink) }
    val code = remember(type.body) { type.body.copy(fontFamily = FontFamily.Monospace) }
    val strong = remember(colors.ink) { SpanStyle(fontWeight = FontWeight.SemiBold, color = colors.ink) }
    val settingsHolder = remember { arrayOfNulls<AnnotatorSettings>(1) }
    val annotator = remember(strong) {
        markdownAnnotator { content, child ->
            val settings = settingsHolder[0]
            if (child.type == MarkdownElementTypes.STRONG && settings != null) {
                withStyle(strong) { buildMarkdownAnnotatedString(content, child, settings) }
                true
            } else {
                false
            }
        }
    }
    val uriHandler = LocalUriHandler.current
    remember(links, annotator, uriHandler) {
        DefaultAnnotatorSettings(
            linkTextSpanStyle = links,
            codeSpanStyle = CODE_SPAN_STYLE,
            annotator = annotator,
            linkInteractionListener = LinkInteractionListener { link ->
                (link as? LinkAnnotation.Url)?.url?.let { url -> runCatching { uriHandler.openUri(url) } }
            }
        ).also { settingsHolder[0] = it }
    }
    val streamingComponents = tail?.let { streamingTail ->
        markdownComponents(
            checkbox = { model -> MarkdownCheckBox(model.content, model.node, model.typography.text) },
            paragraph = { model -> StreamingParagraph(model = model, tail = streamingTail, fadeColor = colors.body) }
        )
    }
    val cursorContent = tail?.let { streamingTail ->
        markdownInlineContent(
            mapOf(
                ANSWER_CURSOR_ID to InlineTextContent(
                    Placeholder(CURSOR_SLOT_WIDTH, CURSOR_HEIGHT, PlaceholderVerticalAlign.TextCenter)
                ) { AnswerCursor(blink = streamingTail.blink, alpha = streamingTail.cursorAlpha) }
            )
        )
    }
    Markdown(
        markdownState = markdownState,
        modifier = modifier,
        inlineContent = cursorContent ?: markdownInlineContent(),
        components = streamingComponents ?: markdownComponents(
            checkbox = { model -> MarkdownCheckBox(model.content, model.node, model.typography.text) }
        ),
        colors = markdownColor(
            text = colors.body,
            codeBackground = colors.tile,
            inlineCodeBackground = colors.tile,
            dividerColor = colors.line,
            tableBackground = colors.tile
        ),
        typography = markdownTypography(
            h1 = heading,
            h2 = heading,
            h3 = heading,
            h4 = heading,
            h5 = heading,
            h6 = heading,
            text = type.body,
            code = code,
            inlineCode = code,
            quote = type.body,
            paragraph = type.body,
            ordered = type.body,
            bullet = type.body,
            list = type.body,
            textLink = links,
            table = type.body
        ),
        padding = markdownPadding(
            block = GiSpace.x1,
            list = GiSpace.x1,
            listItemTop = GiSpace.x1,
            listItemBottom = GiSpace.x1,
            listIndent = GiSpace.x2
        ),
        annotator = annotator
    )
}

private val CODE_SPAN_STYLE = SpanStyle(fontFamily = FontFamily.Monospace)
