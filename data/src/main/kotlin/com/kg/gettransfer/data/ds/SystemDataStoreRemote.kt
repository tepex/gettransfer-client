package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.SystemRemote
import com.kg.gettransfer.data.SystemDataStore

import com.kg.gettransfer.data.model.ConfigsEntity
import com.kg.gettransfer.data.model.EndpointEntity

import com.kg.gettransfer.sys.data.MobileConfigsEntity

import org.koin.core.inject

/**
 * Implementation of the [SystemDataStore] interface to provide a means of communicating with the remote data source
 */
open class SystemDataStoreRemote : SystemDataStore {

    private val remote: SystemRemote by inject()

    override suspend fun getConfigs() = remote.getConfigs()

    override suspend fun setConfigs(configsEntity: ConfigsEntity) {
        throw UnsupportedOperationException()
    }

    override suspend fun getMobileConfigs() = remote.getMobileConfigs()

    override suspend fun setMobileConfigs(mobileConfigsEntity: MobileConfigsEntity) {
        throw UnsupportedOperationException()
    }

    override fun changeEndpoint(endpoint: EndpointEntity) = remote.changeEndpoint(endpoint)
}
