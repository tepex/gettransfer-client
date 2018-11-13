package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.RemoteException
import com.kg.gettransfer.data.SystemRemote
import com.kg.gettransfer.data.SystemDataStore

import com.kg.gettransfer.data.mapper.ExceptionMapper

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ConfigsEntity
import com.kg.gettransfer.data.model.EndpointEntity

/**
 * Implementation of the [SystemDataStore] interface to provide a means of communicating with the remote data source
 */
open class SystemDataStoreRemote(private val remote: SystemRemote): SystemDataStore {
    override suspend fun getConfigs() = remote.getConfigs()
    override suspend fun setConfigs(configsEntity: ConfigsEntity) { throw UnsupportedOperationException() }

    override suspend fun getAccount() = remote.getAccount()
    override suspend fun setAccount(accountEntity: AccountEntity) = remote.setAccount(accountEntity)    
    override suspend fun login(email: String, password: String) = remote.login(email, password)
    override fun clearAccount() { throw UnsupportedOperationException() }

    override fun changeEndpoint(endpoint: EndpointEntity) = remote.changeEndpoint(endpoint)
}
