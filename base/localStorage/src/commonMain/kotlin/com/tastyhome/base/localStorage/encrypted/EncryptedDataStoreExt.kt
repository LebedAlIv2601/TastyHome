package com.tastyhome.base.localStorage.encrypted

import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory as GoogleDataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.core.okio.OkioSerializer
import androidx.datastore.core.okio.OkioStorage
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesSerializer
import com.tastyhome.base.localStorage.DataStoreFactory
import okio.FileSystem
import okio.SYSTEM

fun DataStoreFactory.createEncrypted(
    filename: String,
    corruptionHandler: ReplaceFileCorruptionHandler<Preferences>? = null,
    migrations: List<DataMigration<Preferences>> = listOf(),
): DataStore<Preferences> {
    val associatedData = filename.encodeToByteArray()

    return GoogleDataStoreFactory.create(
        storage = OkioStorage(
            fileSystem = FileSystem.SYSTEM,
            serializer = PreferencesSerializer.encrypted(associatedData),
            producePath = { getDocumentPath("$filename.preferences_pb") }
        ),
        corruptionHandler = corruptionHandler,
        migrations = migrations,
    )
}

fun <T> DataStoreFactory.createEncrypted(
    filename: String,
    serializer: OkioSerializer<T>,
    corruptionHandler: ReplaceFileCorruptionHandler<T>? = null,
    migrations: List<DataMigration<T>> = listOf(),
): DataStore<T> {
    val associatedData = filename.encodeToByteArray()

    return GoogleDataStoreFactory.create(
        storage = OkioStorage(
            fileSystem = FileSystem.SYSTEM,
            serializer = serializer.encrypted(associatedData),
            producePath = { getDocumentPath(filename) }
        ),
        corruptionHandler = corruptionHandler,
        migrations = migrations,
    )
}