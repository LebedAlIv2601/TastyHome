package com.tastyhome.shared.di

import android.content.Context
import com.tastyhome.base.localStorage.AndroidDataStoreFactory
import com.tastyhome.base.localStorage.DataStoreFactory
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides

@BindingContainer
internal object StorageBindings {
    @Provides
    internal fun dataStoreFactory(context: Context): DataStoreFactory {
        return AndroidDataStoreFactory(context)
    }
}