package com.lebedaliv2601.core.network.baseClient.domain

import com.lebedaliv2601.base.network.httpClient.models.domain.Domain
import com.lebedaliv2601.base.network.httpClient.models.domain.NetworkEnvironment

interface MyDomain : Domain {
    val prod: String

    override fun provideUrl(env: NetworkEnvironment): String {
        return when (env) {
            is MyNetworkEnvironment.Prod -> prod
            else -> prod
        }
    }
}