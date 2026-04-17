package com.lebedaliv2601.core.language.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey

val LANGUAGE_TAG_PREFERENCE = stringPreferencesKey("language_tag")

internal class LanguageDataStore(
    dataStore: DataStore<Preferences>
) : DataStore<Preferences> by dataStore