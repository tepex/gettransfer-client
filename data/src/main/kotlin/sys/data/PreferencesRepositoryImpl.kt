package com.kg.gettransfer.sys.data

import com.kg.gettransfer.core.data.CacheStrategy
import com.kg.gettransfer.core.data.MutableDataSource
import com.kg.gettransfer.core.data.ResultData
import com.kg.gettransfer.core.domain.Result

import com.kg.gettransfer.sys.domain.Preferences
import com.kg.gettransfer.sys.domain.PreferencesRepository

class PreferencesRepositoryImpl(
    private val cache: MutableDataSource<PreferencesEntity?>,
    private val empty: Preferences,
    private val map: (PreferencesEntity) -> Preferences
) : PreferencesRepository {

    var memoryCache: Preferences? = null
        private set

    override suspend fun getResult(): Result<Preferences> {
        memoryCache?.let { return Result.Success(it) }

        val resultCache = cache.getResult()
        if (resultCache is ResultData.Success) {
            var cacheData: Preferences? = resultCache.data?.let { map(it) }
            // create new record in DB
            if (cacheData == null) {
                cacheData = empty
                cache.put(cacheData.map())
            }
            return Result.Success(cacheData)
        } else {
            error("Cached data is not success")
        }
    }

    override suspend fun put(value: Preferences) {
        memoryCache = value
        cache.put(value.map())
    }

    override suspend fun clearCache() {
        clearMemoryCache()
        cache.clear()
    }

    override fun clearMemoryCache() {
        memoryCache = null
    }
}
