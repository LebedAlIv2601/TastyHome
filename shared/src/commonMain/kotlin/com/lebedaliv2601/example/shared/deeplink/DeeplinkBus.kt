package com.lebedaliv2601.example.shared.deeplink

import com.lebedaliv2601.base.foundation.uri.Uri
import com.lebedaliv2601.base.logger.L
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

@Inject
internal class DeeplinkBus {
    private val channel = Channel<Uri>(
        capacity = Channel.CONFLATED,
        onUndeliveredElement = { L.w("Intent $it wasn't handled") },
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val deeplinkFlow: Flow<Uri> = channel.receiveAsFlow()

    fun newDeeplink(uri: Uri) {
        channel.trySend(uri)
    }
}