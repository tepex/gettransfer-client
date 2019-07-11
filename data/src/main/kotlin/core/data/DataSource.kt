package com.kg.gettransfer.core.data

interface DataSource<out T : Any> {
    suspend fun get(): T?
}
