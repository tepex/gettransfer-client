package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.SystemCache
import com.kg.gettransfer.data.SystemDataStore

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ConfigsEntity
import com.kg.gettransfer.data.model.EndpointEntity

/**
 * Implementation of the [SystemDataStore] interface to provide a means of communicating with the local data source.
 */
open class SystemDataStoreCache(private val cache: SystemCache): SystemDataStore {
    override suspend fun getConfigs() = cache.getConfigs() //: ConfigsEntity { throw UnsupportedOperationException() }
    override suspend fun setConfigs(configsEntity: ConfigsEntity) = cache.setConfigs(configsEntity)
    override suspend fun getAccount() = cache.getAccount()
    override suspend fun setAccount(accountEntity: AccountEntity) = cache.setAccount(accountEntity)
    override fun clearAccount() = cache.clearAccount()
    
    override suspend fun login(email: String, password: String): AccountEntity { throw UnsupportedOperationException() }
    override fun changeEndpoint(endpoint: EndpointEntity) { throw UnsupportedOperationException() }
}
