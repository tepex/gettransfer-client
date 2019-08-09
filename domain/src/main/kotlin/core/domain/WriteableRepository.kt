package com.kg.gettransfer.core.domain

interface WriteableRepository<T> {
    suspend fun put(value: T)
}
