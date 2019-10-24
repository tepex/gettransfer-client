package com.kg.gettransfer.core.data

import com.kg.gettransfer.core.domain.Result

interface CacheStrategy<E, M> {
    suspend fun getAndCache(default: M, map: (E) -> M): Result<M>
    suspend fun clearCache()
    fun clearMemoryCache()
}
