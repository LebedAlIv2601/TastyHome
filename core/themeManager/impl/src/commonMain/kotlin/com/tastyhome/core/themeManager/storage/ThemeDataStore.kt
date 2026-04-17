package com.tastyhome.core.themeManager.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey

val THEME_TAG_PREFERENCE = stringPreferencesKey("theme_tag")

internal class ThemeDataStore(
    dataStore: DataStore<Preferences>
) : DataStore<Preferences> by dataStore