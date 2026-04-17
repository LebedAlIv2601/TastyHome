package com.tastyhome.core.network.baseClient

import com.tastyhome.base.network.httpClient.HttpClientBuilder
import com.tastyhome.base.network.httpClient.baseUrl
import com.tastyhome.base.network.httpClient.models.Timeouts
import com.tastyhome.base.network.httpClient.models.serialization.JsonSerializationStrategy
import com.tastyhome.base.network.httpClient.serializationStrategy
import com.tastyhome.base.network.httpClient.setting
import com.tastyhome.base.network.httpClient.timeouts
import com.tastyhome.core.network.baseClient.domain.BaseDomain
import com.tastyhome.core.network.baseClient.domain.MyDomain

fun <T : HttpClientBuilder> T.configureBaseClient(domain: MyDomain = BaseDomain): T {
    return this
        .timeouts(Timeouts())
        .serializationStrategy(JsonSerializationStrategy())
        .baseUrl(domain)
        .setting { expectSuccess = true }
}