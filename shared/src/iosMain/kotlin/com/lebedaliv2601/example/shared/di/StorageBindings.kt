package com.lebedaliv2601.example.shared.di

import com.lebedaliv2601.base.localStorage.DataStoreFactory
import com.lebedaliv2601.base.localStorage.IosDataStoreFactory
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides

@BindingContainer
internal object StorageBindings {
    @Provides
    internal fun dataStoreFactory(): DataStoreFactory {
        return IosDataStoreFactory()
    }
}