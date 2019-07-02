package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.SystemCache
import com.kg.gettransfer.data.SystemDataStore

import com.kg.gettransfer.data.model.EndpointEntity
import com.kg.gettransfer.data.model.MobileConfigEntity

import org.koin.core.inject

/**
 * Implementation of the [SessionDataStore] interface to provide a means of communicating with the local data source.
 */
open class SystemDataStoreCache : SystemDataStore {

    private val cache: SystemCache by inject()

    override suspend fun getMobileConfigs() = cache.getMobileConfigs()

    override suspend fun setMobileConfigs(mobileConfigsEntity: MobileConfigEntity) =
        cache.setMobileConfigs(mobileConfigsEntity)

    override fun changeEndpoint(endpoint: EndpointEntity) {
        throw UnsupportedOperationException()
    }
}
