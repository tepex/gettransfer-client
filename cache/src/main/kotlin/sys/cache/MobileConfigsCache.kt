package com.kg.gettransfer.sys.cache

import com.kg.gettransfer.cache.CacheDatabase

import com.kg.gettransfer.core.data.ResultData

import com.kg.gettransfer.sys.data.MobileConfigsCacheDataSource
import com.kg.gettransfer.sys.data.MobileConfigsEntity

import org.koin.core.KoinComponent
import org.koin.core.inject

class MobileConfigsCache : MobileConfigsCacheDataSource, KoinComponent {

    private val db: CacheDatabase by inject()

    override suspend fun getResult(): ResultData<MobileConfigsEntity?> =
        ResultData.Success(db.mobileConfigsDao().selectAll().firstOrNull()?.map())

    override suspend fun put(data: MobileConfigsEntity?) {
        data?.let { db.mobileConfigsDao().update(it.map()) }
    }

    override suspend fun clear() {
        db.mobileConfigsDao().deleteAll()
    }
}
