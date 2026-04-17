package com.tastyhome.core.language

import android.content.Context
import com.tastyhome.base.localStorage.AndroidDataStoreFactory
import com.tastyhome.core.language.api.LanguageApi
import com.tastyhome.core.language.storage.LanguageDataStore
import com.tastyhome.core.language.storage.LanguageStorage

object LanguageDependenciesProvider {

    @Volatile
    private var dataStoreInstance: LanguageDataStore? = null

    internal fun provideDataStore(context: Context): LanguageDataStore =
        dataStoreInstance ?: synchronized(this) {
            dataStoreInstance ?: LanguageDataStore(AndroidDataStoreFactory(context).create("language_store"))
                .also { dataStoreInstance = it }
        }

    fun provideLanguageApi(context: Context): LanguageApi {
        return LanguageApiImpl(LanguageStorage(LanguageDataStore(provideDataStore(context))))
    }
}