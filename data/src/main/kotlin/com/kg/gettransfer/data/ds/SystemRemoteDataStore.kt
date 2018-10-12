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
open class SystemRemoteDataStore(private val remote: SystemRemote): SystemDataStore {
    override suspend fun getConfigs(): ConfigsEntity {
        try { return remote.getConfigs() }
        catch(e: RemoteException) { throw ExceptionMapper.map(e) }
    }
    override suspend fun getAccount() = remote.getAccount()
    override suspend fun setAccount(accountEntity: AccountEntity) = remote.setAccount(accountEntity)
    override fun clearAccount() { throw UnsupportedOperationException() }
    override suspend fun login(email: String, password: String) = remote.login(email, password)
    fun changeEndpoint(endpoint: EndpointEntity) = remote.changeEndpoint(endpoint)
}
