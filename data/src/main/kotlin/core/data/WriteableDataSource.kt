package com.kg.gettransfer.core.data

interface WriteableDataSource<T> {
    suspend fun put(data: T)
    suspend fun clear()
}
