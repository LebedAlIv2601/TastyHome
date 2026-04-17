package com.tastyhome.shared.di

import com.tastyhome.base.localStorage.DataStoreFactory
import com.tastyhome.base.localStorage.IosDataStoreFactory
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides

@BindingContainer
internal object StorageBindings {
    @Provides
    internal fun dataStoreFactory(): DataStoreFactory {
        return IosDataStoreFactory()
    }
}