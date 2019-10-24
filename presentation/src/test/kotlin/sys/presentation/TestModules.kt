package com.kg.gettransfer.sys.presentation

import com.kg.gettransfer.di.ENDPOINTS
import com.kg.gettransfer.sys.domain.Endpoint

import org.koin.dsl.module
import org.koin.core.qualifier.named

val testEndpoints = module {
    single<List<Endpoint>>(named(ENDPOINTS)) {
        listOf(
            Endpoint("test1", "key1", "url1", true, false),
            Endpoint("test2", "key2", "url2", false, false),
            Endpoint("test3", "key3", "url3", false, true)
        )
    }
    single<Endpoint> {
        val endpoints = get<List<Endpoint>>(named(ENDPOINTS))
        endpoints[0]
    }
}
