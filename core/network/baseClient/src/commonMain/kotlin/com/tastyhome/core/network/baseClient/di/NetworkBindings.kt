package com.tastyhome.core.network.baseClient.di

import com.tastyhome.base.network.httpClient.HttpClientBuilder
import com.tastyhome.base.network.httpClient.HttpClientSetting
import com.tastyhome.base.network.httpClient.models.domain.NetworkEnvironment
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class BaseClientBuilder

@BindingContainer(includes = [InternalBindings::class])
object NetworkBindings

@BindingContainer
internal object InternalBindings {
    @Provides
    fun provideHttpClientBuilder(
        environment: NetworkEnvironment
    ): HttpClientBuilder {
        return object : HttpClientBuilder {
            override val environment: NetworkEnvironment = environment
            override val settings: MutableList<HttpClientSetting> = mutableListOf()
        }
    }
}