package com.kg.gettransfer.core.data

interface MutableDataSource<T : Any> : DataSource<T> {
    suspend fun put(data: T)
}
