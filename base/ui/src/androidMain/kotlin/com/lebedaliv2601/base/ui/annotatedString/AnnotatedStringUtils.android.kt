package com.lebedaliv2601.base.ui.annotatedString

import android.graphics.Typeface
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.core.text.HtmlCompat

private const val XML_DIVIDER = "\n\n"
private const val HTML_DIVIDER = "<br/><br/>"

actual fun String.parseHtmlAsAnnotatedString(defaultColor: Color, linkColor: Color): AnnotatedString {
    val textWithPreservedNewlines = this.replace(XML_DIVIDER, HTML_DIVIDER)
    return buildAnnotatedString {
        val spanned =
            HtmlCompat.fromHtml(textWithPreservedNewlines, HtmlCompat.FROM_HTML_MODE_COMPACT)
        append(spanned)
        addStyle(
            style = SpanStyle(color = defaultColor),
            start = 0,
            end = spanned.length
        )
        spanned.getSpans(0, spanned.length, URLSpan::class.java)
            .forEach { span ->
                val start = spanned.getSpanStart(span)
                val end = spanned.getSpanEnd(span)
                addStringAnnotation(
                    tag = "URL",
                    annotation = span.url,
                    start = start,
                    end = end
                )
                addStyle(
                    style = SpanStyle(color = linkColor),
                    start = start,
                    end = end
                )
            }
        spanned.getSpans(0, spanned.length, StyleSpan::class.java)
            .forEach { span ->
                if (span.style == Typeface.BOLD) {
                    val start = spanned.getSpanStart(span)
                    val end = spanned.getSpanEnd(span)
                    addStyle(
                        style = SpanStyle(fontWeight = FontWeight.Bold),
                        start = start,
                        end = end
                    )
                }
            }
        spanned.getSpans(0, spanned.length, ForegroundColorSpan::class.java)
            .forEach { span ->
                val start = spanned.getSpanStart(span)
                val end = spanned.getSpanEnd(span)
                addStyle(
                    style = SpanStyle(color = Color(span.foregroundColor)),
                    start = start,
                    end = end,
                )
            }
    }
}