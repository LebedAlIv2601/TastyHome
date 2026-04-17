package com.tastyhome.core.database.di

import android.content.Context
import androidx.room.Room
import com.tastyhome.core.database.AppDatabase
import com.tastyhome.core.database.PERSISTENT_DATABASE_FILE
import com.tastyhome.core.database.getRoomDatabase
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