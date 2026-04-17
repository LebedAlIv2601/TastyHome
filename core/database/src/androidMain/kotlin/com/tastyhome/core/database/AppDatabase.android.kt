package com.tastyhome.core.database

import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.AndroidSQLiteDriver

internal actual fun platformDriver(): SQLiteDriver = AndroidSQLiteDriver()