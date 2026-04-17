package com.tastyhome.core.network.baseClient.domain

import com.tastyhome.base.network.httpClient.models.domain.NetworkEnvironment

sealed class MyNetworkEnvironment (override val key: String) : NetworkEnvironment {
    data object Prod : MyNetworkEnvironment("prod")
}