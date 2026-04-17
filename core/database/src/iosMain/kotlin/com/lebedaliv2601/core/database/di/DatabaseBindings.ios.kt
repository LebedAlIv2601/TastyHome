package com.lebedaliv2601.core.database.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.lebedaliv2601.core.database.AppDatabase
import com.lebedaliv2601.core.database.PERSISTENT_DATABASE_FILE
import com.lebedaliv2601.core.database.getRoomDatabase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@BindingContainer
abstract class IosDatabaseBindings {
    companion object {
        @SingleIn(AppScope::class)
        @Provides
        internal fun provideAppDatabase(): AppDatabase {
            return getRoomDatabase(getDatabaseBuilder())
        }
    }
}

private fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFilePath = documentDirectory() + "/$PERSISTENT_DATABASE_FILE"
    return Room.databaseBuilder<AppDatabase>(name = dbFilePath)
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}

actual typealias DatabaseBindings = IosDatabaseBindings