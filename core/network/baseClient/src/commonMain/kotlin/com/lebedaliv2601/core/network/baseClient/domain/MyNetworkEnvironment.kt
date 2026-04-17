package com.lebedaliv2601.core.network.baseClient.domain

import com.lebedaliv2601.base.network.httpClient.models.domain.NetworkEnvironment

sealed class MyNetworkEnvironment (override val key: String) : NetworkEnvironment {
    data object Prod : MyNetworkEnvironment("prod")
}