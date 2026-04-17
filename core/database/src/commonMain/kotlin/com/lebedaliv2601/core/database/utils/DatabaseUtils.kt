package com.lebedaliv2601.core.database.utils

import androidx.room.RoomDatabase
import androidx.room.immediateTransaction
import androidx.room.useWriterConnection

suspend fun <T> RoomDatabase.withTransaction(block: suspend () -> T): T {
    return this.useWriterConnection {
        it.immediateTransaction { block() }
    }
}