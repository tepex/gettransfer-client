package com.kg.gettransfer.sys.data

import com.kg.gettransfer.sys.domain.Endpoint

data class EndpointEntity(
    val name: String,
    val key: String,
    val url: String,
    val isDemo: Boolean = false,
    val isDev: Boolean = false
) {
    companion object {
        const val NAME    = "name"
        const val KEY     = "key"
        const val URL     = "url"
        const val IS_DEMO = "is_demo"
        const val IS_DEV  = "is_dev"
    }
}

fun Endpoint.map() = EndpointEntity(name, key, url, isDemo, isDev)
fun EndpointEntity.map() = Endpoint(name, key, url, isDemo, isDev)
