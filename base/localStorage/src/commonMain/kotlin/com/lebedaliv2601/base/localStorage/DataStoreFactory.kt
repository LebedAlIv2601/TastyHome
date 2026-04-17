package com.lebedaliv2601.base.localStorage

import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.core.okio.OkioSerializer
import androidx.datastore.core.okio.OkioStorage
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.FileSystem
import okio.Path
import okio.SYSTEM

abstract class DataStoreFactory {
    fun <T> create(
        filename: String,
        serializer: OkioSerializer<T>,
        corruptionHandler: ReplaceFileCorruptionHandler<T>? = null,
        migrations: List<DataMigration<T>> = emptyList(),
    ): DataStore<T> {
        return DataStoreFactory.create(
            storage = OkioStorage(
                fileSystem = FileSystem.SYSTEM,
                serializer = serializer,
                producePath = { getDocumentPath(filename) }
            ),
            corruptionHandler = corruptionHandler,
            migrations = migrations
        )
    }

    fun create(
        filename: String,
        corruptionHandler: ReplaceFileCorruptionHandler<Preferences>? = null,
        migrations: List<DataMigration<Preferences>> = listOf(),
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.createWithPath(
            migrations = migrations,
            corruptionHandler = corruptionHandler,
            produceFile = { getDocumentPath("$filename.preferences_pb") }
        )
    }

    abstract fun getDocumentPath(filename: String): Path
}