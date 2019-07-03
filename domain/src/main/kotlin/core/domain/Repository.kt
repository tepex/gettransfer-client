package com.kg.gettransfer.core.domain

interface Repository<T> {
    fun get(): T
}
