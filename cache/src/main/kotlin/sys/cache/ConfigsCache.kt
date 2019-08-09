package com.kg.gettransfer.sys.cache

import com.kg.gettransfer.cache.CacheDatabase

import com.kg.gettransfer.core.data.ResultData

import com.kg.gettransfer.sys.data.ConfigsCacheDataSource
import com.kg.gettransfer.sys.data.ConfigsEntity

import org.koin.core.inject
import org.koin.core.KoinComponent

class ConfigsCache : ConfigsCacheDataSource, KoinComponent {

    private val db: CacheDatabase by inject()

    override suspend fun getResult(): ResultData<ConfigsEntity?> =
        ResultData.Success(db.configsDao().selectAll().firstOrNull()?.map())

    override suspend fun put(data: ConfigsEntity?) {
        data?.let { db.configsDao().update(it.map()) }
    }

    override suspend fun clear() {
        db.configsDao().deleteAll()
    }
}
