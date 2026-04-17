package com.tastyhome.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.sqlite.SQLiteDriver
import com.tastyhome.base.foundation.coroutines.MyDispatchers
import com.tastyhome.core.database.converters.DateTimeDBConverter
import com.tastyhome.core.database.entity.test.TestDao
import com.tastyhome.core.database.entity.test.TestEntity
import com.tastyhome.core.database.migrations.Migrations

internal const val PERSISTENT_DATABASE_FILE = "com.tastyhome.appdatabase"

@Database(
    entities = [
        TestEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(
    DateTimeDBConverter::class
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun testEntityDao(): TestDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
internal expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

internal fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>,
): AppDatabase {
    return builder
        .addMigrations(*Migrations.manualMigrations)
        .setDriver(platformDriver())
        .setQueryCoroutineContext(MyDispatchers.IO)
        .build()
}

internal expect fun platformDriver(): SQLiteDriver