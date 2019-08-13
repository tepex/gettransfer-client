package com.kg.gettransfer.sys.domain

data class Endpoint(
    val name: String,
    val key: String,
    val url: String,
    val isDemo: Boolean,
    val isDev: Boolean
) {

    override fun hashCode() = key.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is Endpoint) return false
        return other.key == key
    }
}
