package com.lebedaliv2601.base.platform.clipboard

import android.content.ClipData
import android.content.ClipboardManager as AndroidClipboardManager
import android.content.Context

internal class AndroidClipboardManager(context: Context) : ClipboardManager {

    private val systemClipboard by lazy {
        context.getSystemService(Context.CLIPBOARD_SERVICE) as AndroidClipboardManager
    }

    override fun getText(): String? {
        val clip = systemClipboard.primaryClip ?: return null
        if (clip.itemCount == 0) return null
        return clip.getItemAt(0).text?.toString()
    }

    override fun copy(text: String) {
        systemClipboard.setPrimaryClip(ClipData.newPlainText(null, text))
    }
}
