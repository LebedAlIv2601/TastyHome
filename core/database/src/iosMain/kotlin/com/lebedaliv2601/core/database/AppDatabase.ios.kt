package com.lebedaliv2601.core.database

import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.NativeSQLiteDriver

internal actual fun platformDriver(): SQLiteDriver = NativeSQLiteDriver()