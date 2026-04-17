package com.lebedaliv2601.base.localStorage

import android.content.Context
import okio.Path
import okio.Path.Companion.toOkioPath
import java.io.File

class AndroidDataStoreFactory(internal val context: Context) : DataStoreFactory() {
    override fun getDocumentPath(filename: String): Path {
        return File(context.filesDir, "datastore/$filename").toPath().toOkioPath()
    }
}