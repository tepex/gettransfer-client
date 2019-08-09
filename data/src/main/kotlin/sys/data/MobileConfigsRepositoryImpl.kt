package com.kg.gettransfer.sys.data

import com.kg.gettransfer.core.data.CacheStrategy
import com.kg.gettransfer.core.domain.Result

import com.kg.gettransfer.sys.domain.MobileConfigs
import com.kg.gettransfer.sys.domain.MobileConfigsRepository

class MobileConfigsRepositoryImpl(
    private val cacheStrategy: CacheStrategy<MobileConfigsEntity, MobileConfigs>,
    private val empty: MobileConfigs,
    private val map: (MobileConfigsEntity) -> MobileConfigs
) : MobileConfigsRepository {

    override suspend fun getResult(): Result<MobileConfigs> = cacheStrategy.getAndCache(empty, map)

    override suspend fun clearCache() {
        cacheStrategy.clearCache()
    }

    override fun clearMemoryCache() {
        cacheStrategy.clearMemoryCache()
    }
}
