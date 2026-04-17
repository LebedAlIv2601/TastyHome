package com.lebedaliv2601.base.ui.annotatedString

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight

private const val XML_DIVIDER = "\n\n"
private const val HTML_DIVIDER = "<br/><br/>"
private val HTML_TAG_REGEX = Regex("<[^>]+>")

actual fun String.parseHtmlAsAnnotatedString(defaultColor: Color, linkColor: Color): AnnotatedString {
    val textWithPreservedNewlines = this.replace(XML_DIVIDER, HTML_DIVIDER)
    val plainText = textWithPreservedNewlines
        .replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n")
        .replace(HTML_TAG_REGEX, "")
        .replace("&nbsp;", " ")
        .replace("&amp;", "&")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")

    return buildAnnotatedString {
        append(plainText)
        addStyle(
            style = SpanStyle(color = defaultColor),
            start = 0,
            end = plainText.length
        )
        val htmlLinkRegex = Regex("""<a[^>]*href=["']([^"']*)["'][^>]*>([^<]*)</a>""", RegexOption.IGNORE_CASE)
        htmlLinkRegex.findAll(textWithPreservedNewlines).forEach { match ->
            val url = match.groupValues[1]
            val linkText = match.groupValues[2].replace(HTML_TAG_REGEX, "")
            val start = plainText.indexOf(linkText)
            if (start != -1 && linkText.isNotEmpty()) {
                val end = start + linkText.length
                addStringAnnotation("URL", url, start, end)
                addStyle(SpanStyle(color = linkColor), start, end)
            }
        }
        val boldRegex = Regex("""<b>([^<]*)</b>""", RegexOption.IGNORE_CASE)
        boldRegex.findAll(textWithPreservedNewlines).forEach { match ->
            val boldText = match.groupValues[1].replace(HTML_TAG_REGEX, "")
            val start = plainText.indexOf(boldText)
            if (start != -1 && boldText.isNotEmpty()) {
                addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, start + boldText.length)
            }
        }
    }
}