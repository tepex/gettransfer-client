package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.SystemRemote
import com.kg.gettransfer.data.SystemDataStore
import com.kg.gettransfer.data.model.MobileConfigEntity

import org.koin.core.inject

/**
 * Implementation of the [SystemDataStore] interface to provide a means of communicating with the remote data source
 */
open class SystemDataStoreRemote : SystemDataStore {

    private val remote: SystemRemote by inject()

    override suspend fun getMobileConfigs() = remote.getMobileConfigs()

    override suspend fun setMobileConfigs(mobileConfigsEntity: MobileConfigEntity) {
        throw UnsupportedOperationException()
    }
}
