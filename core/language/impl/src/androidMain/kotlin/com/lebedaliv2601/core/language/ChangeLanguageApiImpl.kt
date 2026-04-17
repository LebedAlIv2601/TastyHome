package com.lebedaliv2601.core.language

import android.content.Context
import com.jakewharton.processphoenix.ProcessPhoenix
import com.lebedaliv2601.base.foundation.coroutines.MyDispatchers
import com.lebedaliv2601.core.language.api.AppLanguage
import com.lebedaliv2601.core.language.api.ChangeLanguageApi
import com.lebedaliv2601.core.language.storage.LanguageStorage
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext

@Inject
internal class ChangeLanguageApiImpl(
    private val context: Context,
    private val storage: LanguageStorage
) : ChangeLanguageApi {
    override suspend fun changeLanguage(language: AppLanguage) {
        storage.saveLanguage(language.value)
        withContext(MyDispatchers.Main) {
            ProcessPhoenix.triggerRebirth(context)
        }
    }
}