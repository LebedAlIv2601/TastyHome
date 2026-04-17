package com.lebedaliv2601.example.shared

import com.lebedaliv2601.base.foundation.uri.parseUri
import com.lebedaliv2601.example.shared.root.RootComponent

fun handleDeeplinkUrl(rootComponent: RootComponent, uriString: String) {
    parseUri(uriString)?.let { rootComponent.newDeeplink(it) }
}