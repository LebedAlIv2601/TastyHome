package com.lebedaliv2601.base.platform.utils

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri

fun Context.startIntentSafe(intent: Intent?, onFailure: (Throwable) -> Unit = {}) {
    runCatching {
        startActivity(intent)
    }.onFailure {
        onFailure(it)
    }
}

fun Context.shareIntent(
    uri: Uri,
    intentType: String,
    onFailure: (Throwable) -> Unit = {},
) {
    val intent = Intent.createChooser(
        Intent().apply {
            action = Intent.ACTION_SEND
            type = intentType
            clipData = ClipData.newRawUri("", uri)
            addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            putExtra(Intent.EXTRA_STREAM, uri)
        },
        null
    )
    startIntentSafe(intent, onFailure)
}