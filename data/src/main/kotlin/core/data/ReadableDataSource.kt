package com.kg.gettransfer.core.data

interface ReadableDataSource<out T> {
    suspend fun getResult(): ResultData<T>
}
