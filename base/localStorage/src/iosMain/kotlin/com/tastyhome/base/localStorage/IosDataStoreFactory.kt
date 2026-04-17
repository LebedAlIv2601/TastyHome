package com.tastyhome.base.localStorage

import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

class IosDataStoreFactory : DataStoreFactory() {
    @OptIn(ExperimentalForeignApi::class)
    override fun getDocumentPath(filename: String): Path {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = true,
            error = null
        )

        val path = requireNotNull(documentDirectory?.path) {
            "Failed to get document directory path"
        }

        return "$path/$filename".toPath()
    }
}
