package com.tastyhome.core.network.baseClient.domain

import com.tastyhome.base.network.httpClient.models.domain.Domain
import com.tastyhome.base.network.httpClient.models.domain.NetworkEnvironment

interface MyDomain : Domain {
    val prod: String

    override fun provideUrl(env: NetworkEnvironment): String {
        return when (env) {
            is MyNetworkEnvironment.Prod -> prod
            else -> prod
        }
    }
}