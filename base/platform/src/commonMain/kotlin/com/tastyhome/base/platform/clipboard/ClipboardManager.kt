package com.tastyhome.base.platform.clipboard

interface ClipboardManager {

    fun getText(): String?

    fun copy(text: String)
}
