package com.kg.gettransfer.core.domain

interface Repository<out T : Any> {
    fun get(): T
}
