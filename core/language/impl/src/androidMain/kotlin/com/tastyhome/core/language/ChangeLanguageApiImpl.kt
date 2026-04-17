package com.tastyhome.core.language

import android.content.Context
import com.jakewharton.processphoenix.ProcessPhoenix
import com.tastyhome.base.foundation.coroutines.MyDispatchers
import com.tastyhome.core.language.api.AppLanguage
import com.tastyhome.core.language.api.ChangeLanguageApi
import com.tastyhome.core.language.storage.LanguageStorage
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