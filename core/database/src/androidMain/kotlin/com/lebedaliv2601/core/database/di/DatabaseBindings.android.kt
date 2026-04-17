package com.lebedaliv2601.core.database.di

import android.content.Context
import androidx.room.Room
import com.lebedaliv2601.core.database.AppDatabase
import com.lebedaliv2601.core.database.PERSISTENT_DATABASE_FILE
import com.lebedaliv2601.core.database.getRoomDatabase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@BindingContainer
abstract class AndroidDatabaseBindings {
    companion object {
        @SingleIn(AppScope::class)
        @Provides
        internal fun provideAppDatabase(context: Context): AppDatabase {
            val builder = Room.databaseBuilder<AppDatabase>(
                context = context,
                name = PERSISTENT_DATABASE_FILE
            )
            return getRoomDatabase(builder)
        }
    }
}

actual typealias DatabaseBindings = AndroidDatabaseBindings