package com.tastyhome.shared

import com.tastyhome.base.foundation.uri.parseUri
import com.tastyhome.shared.root.RootComponent

fun handleDeeplinkUrl(rootComponent: RootComponent, uriString: String) {
    parseUri(uriString)?.let { rootComponent.newDeeplink(it) }
}