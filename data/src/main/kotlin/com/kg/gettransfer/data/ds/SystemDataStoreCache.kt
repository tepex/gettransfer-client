package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.SystemCache
import com.kg.gettransfer.data.SystemDataStore

import com.kg.gettransfer.data.model.ConfigsEntity
import com.kg.gettransfer.data.model.EndpointEntity

import com.kg.gettransfer.sys.data.MobileConfigsEntity

import org.koin.core.inject

/**
 * Implementation of the [SessionDataStore] interface to provide a means of communicating with the local data source.
 */
open class SystemDataStoreCache : SystemDataStore {

    private val cache: SystemCache by inject()

    override suspend fun getConfigs() = cache.getConfigs() // : ConfigsEntity { throw UnsupportedOperationException() }

    override suspend fun setConfigs(configsEntity: ConfigsEntity) = cache.setConfigs(configsEntity)

    override suspend fun getMobileConfigs() = cache.getMobileConfigs()

    override suspend fun setMobileConfigs(mobileConfigsEntity: MobileConfigsEntity) =
        cache.setMobileConfigs(mobileConfigsEntity)

    override fun changeEndpoint(endpoint: EndpointEntity) {
        throw UnsupportedOperationException()
    }
}
