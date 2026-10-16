package com.thejawnpaul.gptinvestor.features.conversation.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.mikepenz.markdown.annotator.AnnotatorSettings
import com.mikepenz.markdown.annotator.annotatorSettings
import com.mikepenz.markdown.annotator.buildMarkdownAnnotatedString
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography
import com.mikepenz.markdown.model.markdownAnnotator
import com.mikepenz.markdown.model.rememberMarkdownState
import com.thejawnpaul.gptinvestor.theme.GiTheme
import org.intellij.markdown.MarkdownElementTypes

@Composable
fun AnswerMarkdown(text: String, modifier: Modifier = Modifier) {
    val colors = GiTheme.colors
    val type = GiTheme.type
    val markdownState = rememberMarkdownState(content = text, retainState = true)
    val links = remember(colors.violet) { TextLinkStyles(SpanStyle(color = colors.violet)) }
    val heading = remember(type.sectionTitle, colors.ink) { type.sectionTitle.copy(color = colors.ink) }
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
    settingsHolder[0] = annotatorSettings(
        linkTextSpanStyle = links,
        codeSpanStyle = CODE_SPAN_STYLE,
        annotator = annotator,
        referenceLinkHandler = null
    )
    Markdown(
        markdownState = markdownState,
        modifier = modifier,
        colors = markdownColor(text = colors.body),
        typography = markdownTypography(
            h1 = heading,
            h2 = heading,
            h3 = heading,
            text = type.body,
            paragraph = type.body,
            ordered = type.body,
            bullet = type.body,
            list = type.body,
            textLink = links
        ),
        annotator = annotator
    )
}

private val CODE_SPAN_STYLE = SpanStyle(fontFamily = FontFamily.Monospace)
