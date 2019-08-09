package com.kg.gettransfer.sys.cache

import com.kg.gettransfer.cache.CacheDatabase

import com.kg.gettransfer.core.data.ResultData

import com.kg.gettransfer.sys.data.PreferencesCacheDataSource
import com.kg.gettransfer.sys.data.PreferencesEntity

import kotlinx.coroutines.delay

import org.koin.core.inject
import org.koin.core.KoinComponent

class PreferencesCache : PreferencesCacheDataSource, KoinComponent {

    private val db: CacheDatabase by inject()

    override suspend fun getResult(): ResultData<PreferencesEntity?> =
        ResultData.Success(db.preferencesDao().selectAll().firstOrNull()?.map())

    override suspend fun put(data: PreferencesEntity?) {
        data?.let { db.preferencesDao().update(it.map()) }
    }

    override suspend fun clear() {
        db.preferencesDao().deleteAll()
    }
}
