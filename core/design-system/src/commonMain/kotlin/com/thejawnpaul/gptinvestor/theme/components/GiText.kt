package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

enum class GiTextStyle {
    Hero,
    Display,
    ScreenTitle,
    PageTitle,
    StatNumber,
    CompanyName,
    SectionTitle,
    Body,
    BodySmall,
    Label,
    LabelRegular,
    Button,
    Link,
    Meta,
    MetaRelaxed,
    Eyebrow,
    Delta,
    Tag,
    Caption,
    NavLabel,
    Input,
    FieldLabel,
    Initials,
    TileInitials
}

@Composable
fun GiText(
    text: String,
    modifier: Modifier = Modifier,
    style: GiTextStyle = GiTextStyle.Body,
    color: Color = style.defaultColor(),
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontWeight = fontWeight,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow,
        style = style.textStyle()
    )
}

@Composable
fun GiTextStyle.textStyle(): TextStyle {
    val type = GiTheme.type
    return when (this) {
        GiTextStyle.Hero -> type.hero
        GiTextStyle.Display -> type.display
        GiTextStyle.ScreenTitle -> type.screenTitle
        GiTextStyle.PageTitle -> type.pageTitle
        GiTextStyle.StatNumber -> type.statNumber
        GiTextStyle.CompanyName -> type.companyName
        GiTextStyle.SectionTitle -> type.sectionTitle
        GiTextStyle.Body -> type.body
        GiTextStyle.BodySmall -> type.bodySmall
        GiTextStyle.Label -> type.label
        GiTextStyle.LabelRegular -> type.labelRegular
        GiTextStyle.Button -> type.button
        GiTextStyle.Link -> type.link
        GiTextStyle.Meta -> type.meta
        GiTextStyle.MetaRelaxed -> type.metaRelaxed
        GiTextStyle.Eyebrow -> type.eyebrow
        GiTextStyle.Delta -> type.delta
        GiTextStyle.Tag -> type.tag
        GiTextStyle.Caption -> type.caption
        GiTextStyle.NavLabel -> type.navLabel
        GiTextStyle.Input -> type.input
        GiTextStyle.FieldLabel -> type.fieldLabel
        GiTextStyle.Initials -> type.initials
        GiTextStyle.TileInitials -> type.tileInitials
    }
}

@Composable
fun GiTextStyle.defaultColor(): Color {
    val colors = GiTheme.colors
    return when (this) {
        GiTextStyle.Body, GiTextStyle.BodySmall, GiTextStyle.Caption -> colors.body
        GiTextStyle.Meta, GiTextStyle.MetaRelaxed, GiTextStyle.Eyebrow, GiTextStyle.NavLabel -> colors.muted
        GiTextStyle.Link -> colors.violet
        else -> colors.ink
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiTextPreview() {
    GiPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
            GiTextStyle.entries.forEach { GiText(text = it.name, style = it) }
        }
    }
}
