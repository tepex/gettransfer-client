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

    override suspend fun setConfigs(configsEntity: ConfigsEntity) { throw UnsupportedOperationException() }
    
    override suspend fun getAccount(): AccountEntity {
        try { return remote.getAccount() }
        catch(e: RemoteException) { throw ExceptionMapper.map(e) }
    }
    
    override suspend fun setAccount(accountEntity: AccountEntity) {
        try { return remote.setAccount(accountEntity) }
        catch(e: RemoteException) { throw ExceptionMapper.map(e) }
    }
    
    override fun clearAccount() { throw UnsupportedOperationException() }
    
    override suspend fun login(email: String, password: String): AccountEntity {
        try { return remote.login(email, password) }
        catch(e: RemoteException) { throw ExceptionMapper.map(e) }
    }
    
    fun changeEndpoint(endpoint: EndpointEntity) = remote.changeEndpoint(endpoint)
}
