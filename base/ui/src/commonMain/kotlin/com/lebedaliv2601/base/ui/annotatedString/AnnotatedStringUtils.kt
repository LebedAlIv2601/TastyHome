package com.lebedaliv2601.base.ui.annotatedString

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import com.lebedaliv2601.base.foundation.other.HTML_LINKS_PATTERN
import com.lebedaliv2601.base.foundation.other.LINKS_PATTERN
import com.lebedaliv2601.base.foundation.other.MARKDOWN_LINK_PATTERN
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

const val CLICKED_URL = "URL"

expect fun String.parseHtmlAsAnnotatedString(defaultColor: Color, linkColor: Color): AnnotatedString

fun String.parseHtmlLinksAsAnnotatedString(defaultColor: Color, linkColor: Color): AnnotatedString {
    return buildAnnotatedString {
        val htmlLinkRegex = Regex(HTML_LINKS_PATTERN)
        val markdownLinkRegex = Regex(MARKDOWN_LINK_PATTERN)

        val htmlLinkMatches = htmlLinkRegex.findAll(this@parseHtmlLinksAsAnnotatedString).toList()
        val markdownLinkMatches = markdownLinkRegex.findAll(this@parseHtmlLinksAsAnnotatedString).toList()

        val plainText = this@parseHtmlLinksAsAnnotatedString
            .replaceHtmlLinks()
            .replaceMarkdownLinks()
            .replace(Regex("<[^>]+>"), "")

        append(plainText)
        addStyle(
            style = SpanStyle(color = defaultColor),
            start = 0,
            end = plainText.length
        )

        addHtmlLinkAnnotations(htmlLinkMatches, plainText, linkColor)
        addMarkdownLinkAnnotations(markdownLinkMatches, plainText, linkColor)
        addPlainLinkAnnotations(plainText, linkColor)
    }
}

private fun String.replaceHtmlLinks(): String {
    val htmlLinkRegex = Regex(HTML_LINKS_PATTERN)
    var text = this
    htmlLinkRegex.findAll(this).forEach { match ->
        val full = match.value
        val display = match.groupValues[2]
        text = text.replace(full, display)
    }
    return text
}

private fun String.replaceMarkdownLinks(): String {
    val markdownLinkRegex = Regex(MARKDOWN_LINK_PATTERN)
    var text = this
    markdownLinkRegex.findAll(this).forEach { match ->
        val full = match.value
        val display = match.groupValues[1]
        text = text.replace(full, display)
    }
    return text
}

private fun AnnotatedString.Builder.addHtmlLinkAnnotations(
    htmlLinkMatches: List<MatchResult>,
    plainText: String,
    linkColor: Color
) {
    for (match in htmlLinkMatches) {
        val url = match.groupValues[1]
        val text = match.groupValues[2]
        val start = plainText.indexOf(text)
        if (start != -1) {
            val end = start + text.length
            addStyle(
                style = SpanStyle(color = linkColor),
                start = start,
                end = end
            )
            addStringAnnotation(CLICKED_URL, url, start, end)
        }
    }
}

private fun AnnotatedString.Builder.addMarkdownLinkAnnotations(
    markdownLinkMatches: List<MatchResult>,
    plainText: String,
    linkColor: Color
) {
    for (match in markdownLinkMatches) {
        val text = match.groupValues[1]
        val url = match.groupValues[2]
        val start = plainText.indexOf(text)
        if (start != -1) {
            val end = start + text.length
            addStyle(
                style = SpanStyle(color = linkColor),
                start = start,
                end = end
            )
            addStringAnnotation(CLICKED_URL, url, start, end)
        }
    }
}

private fun AnnotatedString.Builder.addPlainLinkAnnotations(
    plainText: String,
    linkColor: Color
) {
    val plainLinkRegex = Regex(LINKS_PATTERN)
    for (match in plainLinkRegex.findAll(plainText)) {
        val url = match.value
        val start = match.range.first
        val end = match.range.last + 1
        addStyle(
            style = SpanStyle(color = linkColor),
            start = start,
            end = end
        )
        addStringAnnotation(CLICKED_URL, url, start, end)
    }
}

@Composable
fun AnnotatedString.Builder.append(resource: StringResource) {
    append(stringResource(resource))
}

fun AnnotatedString.Builder.newLine() {
    append("\n")
}

fun AnnotatedString.Builder.doubleNewLine() {
    append("\n\n")
}

fun Modifier.detectLinkClick(
    layoutResult: TextLayoutResult?,
    linkText: AnnotatedString,
    onUrlClick: (String) -> Unit
) = this.pointerInput(Unit) {
    detectTapGestures { offset ->
        layoutResult?.let { layout ->
            val position = layout.getOffsetForPosition(offset)
            linkText
                .getStringAnnotations(CLICKED_URL, position, position)
                .firstOrNull()
                ?.let { annotation -> onUrlClick(annotation.item) }
        }
    }
}