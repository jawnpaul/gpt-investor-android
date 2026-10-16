package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

data class GiLink(val text: String, val onClick: () -> Unit)

enum class GiLinkTextTone { Footer, Legal, Error }

@Composable
fun GiLinkText(
    text: String,
    links: List<GiLink>,
    modifier: Modifier = Modifier,
    tone: GiLinkTextTone = GiLinkTextTone.Footer
) {
    val colors = GiTheme.colors
    val type = GiTheme.type
    val toneStyle = when (tone) {
        GiLinkTextTone.Footer -> GiLinkTextStyle(
            text = type.label.copy(fontWeight = FontWeight.Normal),
            color = colors.muted,
            link = SpanStyle(color = colors.violet, fontWeight = FontWeight.SemiBold)
        )
        GiLinkTextTone.Legal -> GiLinkTextStyle(
            text = type.tag.copy(fontWeight = FontWeight.Normal),
            color = colors.muted,
            link = SpanStyle(
                color = colors.ink,
                fontWeight = FontWeight.Medium,
                textDecoration = TextDecoration.Underline
            )
        )
        GiLinkTextTone.Error -> GiLinkTextStyle(
            text = type.bodySmall,
            color = colors.error,
            link = SpanStyle(
                color = colors.error,
                fontWeight = FontWeight.SemiBold,
                textDecoration = TextDecoration.Underline
            )
        )
    }
    val isError = tone == GiLinkTextTone.Error
    val linkStyles = TextLinkStyles(
        style = toneStyle.link,
        pressedStyle = toneStyle.link.merge(
            SpanStyle(background = toneStyle.link.color.copy(alpha = GiMotion.PRESSED_ALPHA))
        )
    )
    val currentLinks by rememberUpdatedState(links)
    val linkTexts = links.map { it.text }
    val annotated = remember(text, linkTexts, linkStyles) {
        buildAnnotatedString {
            append(text)
            linkTexts.forEach { linkText ->
                val start = text.indexOf(linkText)
                if (start >= 0) {
                    addLink(
                        LinkAnnotation.Clickable(tag = linkText, styles = linkStyles) {
                            currentLinks.firstOrNull { it.text == linkText }?.onClick?.invoke()
                        },
                        start = start,
                        end = start + linkText.length
                    )
                }
            }
        }
    }
    Box(
        modifier = modifier.heightIn(min = if (tone == GiLinkTextTone.Footer) GiSpace.touchTarget else 0.dp),
        contentAlignment = if (isError) Alignment.CenterStart else Alignment.Center
    ) {
        Text(
            text = annotated,
            color = toneStyle.color,
            textAlign = if (isError) TextAlign.Start else TextAlign.Center,
            style = toneStyle.text
        )
    }
}

private data class GiLinkTextStyle(val text: TextStyle, val color: Color, val link: SpanStyle)

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiLinkTextPreview() {
    GiPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x4)) {
            GiLinkText(
                text = "Already have an account? Log in",
                links = listOf(GiLink("Log in") {}),
                modifier = Modifier.fillMaxWidth()
            )
            GiLinkText(
                text = "By continuing you agree to our Terms and Privacy Policy.",
                links = listOf(GiLink("Terms") {}, GiLink("Privacy Policy") {}),
                modifier = Modifier.fillMaxWidth(),
                tone = GiLinkTextTone.Legal
            )
            GiLinkText(
                text = "That email and password don't match. Reset your password.",
                links = listOf(GiLink("Reset your password") {}),
                tone = GiLinkTextTone.Error
            )
        }
    }
}
