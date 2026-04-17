package com.tastyhome.base.platform.clipboard

import platform.UIKit.UIPasteboard

internal class IosClipboardManager : ClipboardManager {

    private val pasteboard = UIPasteboard.generalPasteboard

    override fun getText(): String? {
        return pasteboard.string
    }

    override fun copy(text: String) {
        pasteboard.setString(text)
    }
}
