package com.kg.gettransfer.core.data

import com.kg.gettransfer.core.domain.Result

class SimpleCacheStrategy<E, M>(
    private val cache: MutableDataSource<E?>,
    private val remote: ReadableDataSource<E>
) : CacheStrategy<E, M> {

    var memoryCache: M? = null
        private set

    override suspend fun getAndCache(default: M, map: (E) -> M): Result<M> {
        memoryCache?.let { return Result.Success(it) }

        val resultRemote = remote.getResult()
        return if (resultRemote is ResultData.Success) {
            cache.put(resultRemote.data)
            val model = map(resultRemote.data)
            memoryCache = model
            Result.Success(model)
        } else {
            memoryCache = null
            val resultCache = cache.getResult()
            if (resultCache is ResultData.Success) {
                val cacheData = resultCache.data?.let { map(it) } ?: default
                resultRemote.recover(cacheData)
            } else {
                error("Cached data is not success")
            }
        }
    }

    override suspend fun clearCache() {
        clearMemoryCache()
        cache.clear()
    }

    override fun clearMemoryCache() {
        memoryCache = null
    }
}
