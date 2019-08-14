package com.kg.gettransfer.sys.data

import com.kg.gettransfer.core.data.CacheStrategy
import com.kg.gettransfer.core.domain.Result

import com.kg.gettransfer.sys.domain.Configs
import com.kg.gettransfer.sys.domain.ConfigsRepository

class ConfigsRepositoryImpl(
    private val cacheStrategy: CacheStrategy<ConfigsEntity, Configs>,
    private val empty: Configs,
    private val map: (ConfigsEntity) -> Configs
) : ConfigsRepository {

    override suspend fun getResult(): Result<Configs> = cacheStrategy.getAndCache(empty, map)

    override suspend fun clearCache() {
        cacheStrategy.clearCache()
    }

    override fun clearMemoryCache() {
        cacheStrategy.clearMemoryCache()
    }
}
