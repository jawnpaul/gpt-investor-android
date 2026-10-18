package com.thejawnpaul.gptinvestor.features.company.presentation.ui

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import com.google.common.truth.Truth.assertThat
import com.mikepenz.markdown.annotator.DefaultAnnotatorSettings
import com.mikepenz.markdown.annotator.buildMarkdownAnnotatedString
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser
import org.junit.Test

class CustomRichTextTest {

    @Test
    fun `keeps text between two dollar amounts`() {
        val text = "Revenue rose from \$5 billion to \$7 billion."
        val paragraph = MarkdownParser(GFMFlavourDescriptor()).buildMarkdownTreeFromString(text).children.first()

        val settings = DefaultAnnotatorSettings(
            linkTextSpanStyle = TextLinkStyles(),
            codeSpanStyle = SpanStyle(),
            annotator = PLAIN_MATH_ANNOTATOR
        )

        val rendered = buildAnnotatedString { buildMarkdownAnnotatedString(text, paragraph, settings) }

        assertThat(rendered.text).isEqualTo(text)
    }
}
