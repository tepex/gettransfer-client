package com.kg.gettransfer.cache

import com.kg.gettransfer.cache.model.map
import com.kg.gettransfer.data.SystemCache
import com.kg.gettransfer.data.model.MobileConfigEntity

import org.koin.core.inject
import org.koin.core.KoinComponent

class SystemCacheImpl : SystemCache, KoinComponent {

    private val db: CacheDatabase by inject()

    override fun getMobileConfigs() = db.mobileConfigsCachedDao().selectAll().firstOrNull()?.map()

    override fun setMobileConfigs(configs: MobileConfigEntity) = db.mobileConfigsCachedDao().update(configs.map())
}
