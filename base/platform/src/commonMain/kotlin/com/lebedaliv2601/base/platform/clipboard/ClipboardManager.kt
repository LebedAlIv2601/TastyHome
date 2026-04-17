package com.lebedaliv2601.base.platform.clipboard

interface ClipboardManager {

    fun getText(): String?

    fun copy(text: String)
}
