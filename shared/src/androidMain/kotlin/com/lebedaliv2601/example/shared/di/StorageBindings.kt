package com.lebedaliv2601.example.shared.di

import android.content.Context
import com.lebedaliv2601.base.localStorage.AndroidDataStoreFactory
import com.lebedaliv2601.base.localStorage.DataStoreFactory
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides

@BindingContainer
internal object StorageBindings {
    @Provides
    internal fun dataStoreFactory(context: Context): DataStoreFactory {
        return AndroidDataStoreFactory(context)
    }
}