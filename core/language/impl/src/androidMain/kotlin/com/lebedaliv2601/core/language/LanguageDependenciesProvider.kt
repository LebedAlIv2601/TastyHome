package com.lebedaliv2601.core.language

import android.content.Context
import com.lebedaliv2601.base.localStorage.AndroidDataStoreFactory
import com.lebedaliv2601.core.language.api.LanguageApi
import com.lebedaliv2601.core.language.storage.LanguageDataStore
import com.lebedaliv2601.core.language.storage.LanguageStorage

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