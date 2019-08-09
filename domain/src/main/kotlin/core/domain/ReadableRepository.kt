package com.kg.gettransfer.core.domain

interface ReadableRepository<out T> {
    suspend fun getResult(): Result<T>
    suspend fun clearCache()
    fun clearMemoryCache()
}
