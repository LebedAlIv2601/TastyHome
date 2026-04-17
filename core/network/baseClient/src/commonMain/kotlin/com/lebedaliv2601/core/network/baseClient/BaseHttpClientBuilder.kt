package com.lebedaliv2601.core.network.baseClient

import com.lebedaliv2601.base.network.httpClient.HttpClientBuilder
import com.lebedaliv2601.base.network.httpClient.baseUrl
import com.lebedaliv2601.base.network.httpClient.models.Timeouts
import com.lebedaliv2601.base.network.httpClient.models.serialization.JsonSerializationStrategy
import com.lebedaliv2601.base.network.httpClient.serializationStrategy
import com.lebedaliv2601.base.network.httpClient.setting
import com.lebedaliv2601.base.network.httpClient.timeouts
import com.lebedaliv2601.core.network.baseClient.domain.BaseDomain
import com.lebedaliv2601.core.network.baseClient.domain.MyDomain

fun <T : HttpClientBuilder> T.configureBaseClient(domain: MyDomain = BaseDomain): T {
    return this
        .timeouts(Timeouts())
        .serializationStrategy(JsonSerializationStrategy())
        .baseUrl(domain)
        .setting { expectSuccess = true }
}