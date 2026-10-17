package com.thejawnpaul.gptinvestor.features.toppick.presentation.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.chart_description
import com.thejawnpaul.gptinvestor.chart_range
import com.thejawnpaul.gptinvestor.chart_range_1m
import com.thejawnpaul.gptinvestor.chart_range_1m_long
import com.thejawnpaul.gptinvestor.chart_range_1w
import com.thejawnpaul.gptinvestor.chart_range_1w_long
import com.thejawnpaul.gptinvestor.chart_range_1y
import com.thejawnpaul.gptinvestor.chart_range_1y_long
import com.thejawnpaul.gptinvestor.chart_range_3m
import com.thejawnpaul.gptinvestor.chart_range_3m_long
import com.thejawnpaul.gptinvestor.chart_trend_falling
import com.thejawnpaul.gptinvestor.chart_trend_flat
import com.thejawnpaul.gptinvestor.chart_trend_rising
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteResponse
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.HistoricalData
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui.ContentTag
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui.isContentLargeText
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui.overlapMargin
import com.thejawnpaul.gptinvestor.key_pe_ratio
import com.thejawnpaul.gptinvestor.market_cap
import com.thejawnpaul.gptinvestor.read_article
import com.thejawnpaul.gptinvestor.read_less
import com.thejawnpaul.gptinvestor.read_more
import com.thejawnpaul.gptinvestor.sources
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiButtonSize
import com.thejawnpaul.gptinvestor.theme.components.GiButtonVariant
import com.thejawnpaul.gptinvestor.theme.components.GiDivider
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiSegmentedControl
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle
import com.thejawnpaul.gptinvestor.theme.components.textStyle
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled
import com.thejawnpaul.gptinvestor.top_pick_no_news
import com.thejawnpaul.gptinvestor.top_pick_revenue
import com.thejawnpaul.gptinvestor.top_pick_score
import kotlin.math.abs
import kotlin.math.roundToLong
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun PickSectionTitle(text: String, modifier: Modifier = Modifier) {
    GiText(text = text, modifier = modifier.semantics { heading() }, style = GiTextStyle.SectionTitle)
}

@Composable
internal fun ScoreTag(score: Int) {
    val colors = GiTheme.colors
    val (container, content) = when {
        score >= GOOD_SCORE -> colors.gainSoft to colors.gain
        score >= FAIR_SCORE -> colors.cautionSoft to colors.caution
        else -> colors.lossSoft to colors.loss
    }
    ContentTag(text = stringResource(Res.string.top_pick_score, score), container = container, content = content)
}

private const val GOOD_SCORE = 7
private const val FAIR_SCORE = 4

@Composable
internal fun ClampedText(text: String, maxLines: Int, modifier: Modifier = Modifier, onReadMore: (() -> Unit)? = null) {
    val colors = GiTheme.colors
    var expanded by remember(text) { mutableStateOf(false) }
    var overflowing by remember(text) { mutableStateOf(false) }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
        Text(
            text = text,
            modifier = if (isReducedMotionEnabled()) Modifier else Modifier.animateContentSize(GiMotion.smooth()),
            style = GiTextStyle.Body.textStyle(),
            color = colors.body,
            maxLines = if (expanded) Int.MAX_VALUE else maxLines,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { if (!expanded) overflowing = it.hasVisualOverflow }
        )
        if (overflowing || expanded) {
            GiButton(
                text = stringResource(if (expanded) Res.string.read_less else Res.string.read_more),
                onClick = { if (onReadMore != null) onReadMore() else expanded = !expanded },
                modifier = Modifier.overlapMargin(top = GiSpace.x3, bottom = GiSpace.x3),
                size = GiButtonSize.Small,
                variant = GiButtonVariant.Text
            )
        }
    }
}

@Composable
internal fun MetricTiles(metrics: List<String>, modifier: Modifier = Modifier) {
    val perRow = if (isContentLargeText()) 1 else METRICS_PER_ROW
    val rows = remember(metrics, perRow) { metrics.map { displayMetric(parseMetric(it)) }.chunked(perRow) }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(GiSpace.x2)
            ) {
                row.forEach { (label, value) ->
                    MetricTile(label = label, value = value, modifier = Modifier.weight(1f).fillMaxHeight())
                }
                repeat(perRow - row.size) { Box(modifier = Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun MetricTile(label: String?, value: String, modifier: Modifier = Modifier) {
    val colors = GiTheme.colors
    val valueColor = when {
        value.startsWith("+") -> colors.gain
        value.startsWith("-") || value.startsWith("−") -> colors.loss
        else -> colors.ink
    }
    Column(
        modifier = modifier
            .clip(GiRadius.sm)
            .background(colors.tile)
            .padding(GiSpace.x3),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x1)
    ) {
        if (label != null) {
            GiText(text = label, style = GiTextStyle.Meta, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        GiText(text = value, style = GiTextStyle.StatNumber, color = valueColor)
    }
}

private const val METRICS_PER_ROW = 3

internal fun displayMetric(metric: Pair<String?, String>): Pair<String?, String> {
    val (label, value) = metric
    val key = label?.lowercase().orEmpty()
    val shortLabel = METRIC_LABELS[key] ?: label?.let(::sentenceCase)
    val number = value.replace(",", "").toDoubleOrNull()
    val shownValue = when {
        number != null && abs(number) >= THOUSAND -> {
            val money = MONEY_WORDS.any { it in key }
            (if (money) "$" else "") + abbreviate(number)
        }
        ("growth" in key || "change" in key) && value.endsWith("%") && value.first().isDigit() -> "+$value"
        else -> value
    }
    return shortLabel to shownValue
}

private fun abbreviate(value: Double): String {
    val absolute = abs(value)
    val (scaled, suffix) = when {
        absolute >= TRILLION -> value / TRILLION to "T"
        absolute >= BILLION -> value / BILLION to "B"
        absolute >= MILLION -> value / MILLION to "M"
        else -> value / THOUSAND to "K"
    }
    return "${decimal(scaled, 2)}$suffix"
}

private fun sentenceCase(label: String): String = label.split(" ").mapIndexed { index, word ->
    when {
        index == 0 || word.any { it == '/' } || word.count { it.isUpperCase() } > 1 -> word
        else -> word.lowercase()
    }
}.joinToString(" ")

private val METRIC_LABELS = mapOf(
    "revenue growth" to "Growth",
    "gross margins" to "Gross margin",
    "net margins" to "Net margin",
    "operating margins" to "Operating margin",
    "profit margins" to "Profit margin"
)

private val MONEY_WORDS =
    listOf("revenue", "income", "cap", "cash", "earnings", "profit", "sales", "debt", "ebitda", "value")

private const val THOUSAND = 1_000.0

internal fun parseMetric(metric: String): Pair<String?, String> {
    val index = metric.indexOf(':')
    return if (index in 1 until metric.lastIndex) {
        metric.substring(0, index).trim() to metric.substring(index + 1).trim()
    } else {
        null to metric.trim()
    }
}

@Composable
internal fun RiskList(risks: List<String>, modifier: Modifier = Modifier) {
    val colors = GiTheme.colors
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
        risks.forEach { risk ->
            Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
                Spacer(
                    modifier = Modifier
                        .padding(top = GiSpace.x2)
                        .size(RISK_MARKER)
                        .drawWithCache {
                            val unit = size.width / RISK_MARKER_UNITS
                            val path = Path().apply {
                                moveTo(4f * unit, 7f * unit)
                                lineTo(7.5f * unit, 1.5f * unit)
                                lineTo(0.5f * unit, 1.5f * unit)
                                close()
                            }
                            onDrawBehind { drawPath(path = path, color = colors.loss) }
                        }
                )
                GiText(text = risk, modifier = Modifier.weight(1f), style = GiTextStyle.Body)
            }
        }
    }
}

private val RISK_MARKER = 8.dp
private const val RISK_MARKER_UNITS = 8f

internal enum class ChartRange(val points: Int, val label: StringResource, val longLabel: StringResource) {
    Week(5, Res.string.chart_range_1w, Res.string.chart_range_1w_long),
    Month(22, Res.string.chart_range_1m, Res.string.chart_range_1m_long),
    Quarter(66, Res.string.chart_range_3m, Res.string.chart_range_3m_long),
    Year(252, Res.string.chart_range_1y, Res.string.chart_range_1y_long)
}

@Composable
internal fun PriceChartSection(companyName: String, history: List<HistoricalData>, modifier: Modifier = Modifier) {
    var rangeIndex by remember { mutableIntStateOf(0) }
    val range = ChartRange.entries[rangeIndex]
    val points = remember(history, range) { history.takeLast(range.points) }
    val rangeLabel = stringResource(Res.string.chart_range)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(GiSpace.x4)) {
        GiSegmentedControl(
            options = ChartRange.entries.map { stringResource(it.label) },
            selectedIndex = rangeIndex,
            onSelect = { rangeIndex = it },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = rangeLabel }
        )
        if (points.size >= 2) {
            val first = points.first().close
            val last = points.last().close
            val direction = stringResource(
                when {
                    last > first -> Res.string.chart_trend_rising
                    last < first -> Res.string.chart_trend_falling
                    else -> Res.string.chart_trend_flat
                }
            )
            val closes = remember(points) { points.map { it.close } }
            PriceChart(
                values = closes,
                description = stringResource(
                    Res.string.chart_description,
                    companyName,
                    stringResource(range.longLabel),
                    direction
                )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clearAndSetSemantics { },
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(points.first(), points[points.size / 2], points.last()).forEach {
                    GiText(text = shortDate(it.date), style = GiTextStyle.Meta)
                }
            }
        }
    }
}

@Composable
private fun PriceChart(values: List<Float>, description: String, modifier: Modifier = Modifier) {
    val colors = GiTheme.colors
    val rising = values.last() >= values.first()
    val lineColor = if (rising) colors.gainVivid else colors.lossVivid
    val fillAlpha = if (colors.isDark) CHART_FILL_DARK else CHART_FILL_LIGHT
    val gridColor = colors.line
    val dotEdge = colors.raised
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(CHART_HEIGHT)
            .semantics { contentDescription = description }
            .drawWithCache {
                val inset = CHART_DOT_RADIUS.toPx()
                val width = size.width
                val height = size.height
                val dash = PathEffect.dashPathEffect(floatArrayOf(CHART_DASH_ON.toPx(), CHART_DASH_OFF.toPx()))
                val gridWidth = GiEdge.width.toPx()
                val min = values.min()
                val max = values.max()
                val span = (max - min).takeIf { it > 0f }
                val step = width / (values.size - 1)
                val xs = values.indices.map { it * step }
                val ys = values.map { value ->
                    if (span == null) height / 2f else inset + (1f - (value - min) / span) * (height - inset * 2f)
                }
                val line = Path().apply {
                    moveTo(xs[0], ys[0])
                    for (i in 1 until values.size) {
                        val midX = (xs[i - 1] + xs[i]) / 2f
                        cubicTo(midX, ys[i - 1], midX, ys[i], xs[i], ys[i])
                    }
                }
                val fill = Path().apply {
                    addPath(line)
                    lineTo(xs.last(), height)
                    lineTo(xs.first(), height)
                    close()
                }
                val fillBrush = Brush.verticalGradient(
                    listOf(lineColor.copy(alpha = fillAlpha), lineColor.copy(alpha = 0f))
                )
                val lineStroke = Stroke(width = CHART_LINE.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                val dotStroke = Stroke(width = CHART_LINE.toPx())
                val end = Offset(xs.last(), ys.last())
                onDrawBehind {
                    GRID_LINES.forEach { fraction ->
                        val y = height * fraction
                        drawLine(gridColor, Offset(0f, y), Offset(width, y), gridWidth, pathEffect = dash)
                    }
                    drawPath(path = fill, brush = fillBrush)
                    drawPath(path = line, color = lineColor, style = lineStroke)
                    drawCircle(color = lineColor, radius = inset, center = end)
                    drawCircle(color = dotEdge, radius = inset, center = end, style = dotStroke)
                }
            }
    )
}

private val CHART_HEIGHT = 160.dp
private val CHART_LINE = 2.dp
private val CHART_DOT_RADIUS = 5.dp
private val CHART_DASH_ON = 2.dp
private val CHART_DASH_OFF = 4.dp
private const val CHART_FILL_LIGHT = 0.14f
private const val CHART_FILL_DARK = 0.2f
private val GRID_LINES = listOf(0.25f, 0.5625f)

private val MONTHS = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

private val ISO_DATE = Regex("(\\d{4})-(\\d{2})-(\\d{2})")

internal fun shortDate(raw: String): String {
    val match = ISO_DATE.find(raw) ?: return raw
    val month = match.groupValues[2].toIntOrNull()?.takeIf { it in 1..MONTHS.size } ?: return raw
    val day = match.groupValues[3].toIntOrNull() ?: return raw
    return "$day ${MONTHS[month - 1]}"
}

@Composable
internal fun SourcesRow(count: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = GiTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(GiRadius.md)
            .border(GiEdge.width, colors.line, GiRadius.md)
            .clickable(interactionSource = interactionSource, indication = null, role = Role.Button, onClick = onClick)
            .background(if (isPressed) colors.ink.copy(alpha = GiMotion.PRESSED_ALPHA) else Color.Transparent)
            .heightIn(min = GiSpace.settingsRow)
            .padding(horizontal = GiSpace.x4),
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Outlined.Language,
            contentDescription = null,
            modifier = Modifier.size(GiIconSize.md),
            tint = colors.muted
        )
        GiText(text = stringResource(Res.string.sources), modifier = Modifier.weight(1f), style = GiTextStyle.Label)
        GiText(text = count.toString(), style = GiTextStyle.MetaRelaxed, fontWeight = FontWeight.Medium)
        Icon(GiIcons.caretRight, contentDescription = null, modifier = Modifier.size(GiIconSize.md), tint = colors.ink)
    }
}

@Composable
internal fun KeyRatios(company: CompanyDetailRemoteResponse, modifier: Modifier = Modifier) {
    val rows = listOf(
        KeyRatio(stringResource(Res.string.market_cap), company.marketCap?.let { compactMoney(it.toDouble()) }),
        KeyRatio(stringResource(Res.string.key_pe_ratio), company.peRatio?.let { decimal(it.toDouble(), 2) }),
        KeyRatio(stringResource(Res.string.top_pick_revenue), company.revenue?.let { compactMoney(it.toDouble()) })
    )
    Column(modifier = modifier) {
        rows.forEachIndexed { index, (label, value) ->
            if (index > 0) GiDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = GiSpace.settingsRow)
                    .padding(vertical = GiSpace.x2),
                horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GiText(
                    text = label,
                    modifier = Modifier.weight(1f),
                    style = GiTextStyle.LabelRegular,
                    color = GiTheme.colors.muted
                )
                GiText(text = value ?: EM_DASH, style = GiTextStyle.Label)
            }
        }
    }
}

@Composable
internal fun PickNews(company: CompanyDetailRemoteResponse, onOpen: (String) -> Unit, modifier: Modifier = Modifier) {
    val colors = GiTheme.colors
    val news = company.news.orEmpty()
    Column(modifier = modifier) {
        if (news.isEmpty()) {
            GiText(
                text = stringResource(Res.string.top_pick_no_news, company.name ?: company.ticker),
                modifier = Modifier.padding(vertical = GiSpace.x4),
                style = GiTextStyle.Body
            )
        }
        news.forEachIndexed { index, item ->
            if (index > 0) GiDivider()
            Column(
                modifier = Modifier.padding(vertical = GiSpace.x4),
                verticalArrangement = Arrangement.spacedBy(GiSpace.x2)
            ) {
                GiText(text = item.publisher, style = GiTextStyle.Eyebrow)
                GiText(text = item.title, style = GiTextStyle.Label)
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                Row(
                    modifier = Modifier
                        .overlapMargin(top = GiSpace.x3, bottom = GiSpace.x3)
                        .heightIn(min = GiSpace.touchTarget)
                        .clip(GiRadius.xs)
                        .clickable(interactionSource = interactionSource, indication = null, role = Role.Button) {
                            onOpen(item.link)
                        }
                        .background(
                            if (isPressed) colors.ink.copy(alpha = GiMotion.PRESSED_ALPHA) else Color.Transparent
                        ),
                    horizontalArrangement = Arrangement.spacedBy(GiSpace.x1),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GiText(text = stringResource(Res.string.read_article), style = GiTextStyle.Link)
                    Icon(
                        Icons.AutoMirrored.Outlined.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(GiIconSize.sm),
                        tint = colors.violet
                    )
                }
            }
        }
    }
}

private data class KeyRatio(val label: String, val value: String?)

internal data class PickSource(val name: String, val url: String)

@Composable
internal fun SourceRows(sources: List<PickSource>, onOpen: (String) -> Unit) {
    val colors = GiTheme.colors
    Column {
        sources.forEachIndexed { index, source ->
            if (index > 0) GiDivider()
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(interactionSource = interactionSource, indication = null, role = Role.Button) {
                        onOpen(source.url)
                    }
                    .background(if (isPressed) colors.ink.copy(alpha = GiMotion.PRESSED_ALPHA) else Color.Transparent)
                    .heightIn(min = GiSpace.settingsRow),
                horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(GiSpace.x8)
                        .clip(GiRadius.sm)
                        .background(colors.tile),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Language,
                        contentDescription = null,
                        modifier = Modifier.size(GiIconSize.sm),
                        tint = colors.muted
                    )
                }
                GiText(text = source.name, modifier = Modifier.weight(1f), style = GiTextStyle.LabelRegular)
                Icon(
                    Icons.AutoMirrored.Outlined.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(GiIconSize.sm),
                    tint = colors.muted
                )
            }
        }
    }
}

internal fun formatPrice(price: Double): String {
    val cents = (abs(price) * 100).roundToLong()
    val integer = (cents / 100).toString().reversed().chunked(3).joinToString(",").reversed()
    return "${if (price < 0) "−" else ""}$$integer.${(cents % 100).toString().padStart(2, '0')}"
}

internal fun formatPercent(percent: Double): String {
    val sign = when {
        percent > 0 -> "+"
        percent < 0 -> "−"
        else -> ""
    }
    return "$sign${decimal(abs(percent), 2)}%"
}

private fun compactMoney(value: Double): String {
    val absolute = abs(value)
    val (scaled, suffix) = when {
        absolute >= TRILLION -> value / TRILLION to "T"
        absolute >= BILLION -> value / BILLION to "B"
        absolute >= MILLION -> value / MILLION to "M"
        else -> value to ""
    }
    return "$${decimal(scaled, if (suffix.isEmpty()) 0 else 1)}$suffix"
}

private fun decimal(value: Double, places: Int): String {
    var factor = 1L
    repeat(places) { factor *= 10 }
    val scaled = (abs(value) * factor).roundToLong()
    val integer = scaled / factor
    val fraction = (scaled % factor).toString().padStart(places, '0')
    val sign = if (value < 0 && scaled != 0L) "-" else ""
    return if (places == 0) "$sign$integer" else "$sign$integer.$fraction"
}

private const val TRILLION = 1_000_000_000_000.0
private const val BILLION = 1_000_000_000.0
private const val MILLION = 1_000_000.0
private const val EM_DASH = "—"
