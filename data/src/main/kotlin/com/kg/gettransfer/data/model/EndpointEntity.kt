package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.Endpoint

data class EndpointEntity(
    val name: String,
    val key: String,
    val url: String,
    val isDemo: Boolean = false,
    val isDev: Boolean = false
)

fun Endpoint.map() = EndpointEntity(name, key, url, isDemo, isDev)
fun EndpointEntity.map() = Endpoint(name, key, url, isDemo, isDev)
