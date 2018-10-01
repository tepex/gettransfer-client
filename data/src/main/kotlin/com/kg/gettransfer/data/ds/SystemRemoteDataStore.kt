package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.SystemRemote
import com.kg.gettransfer.data.SystemDataStore

import com.kg.gettransfer.data.model.AccountEntity

/**
 * Implementation of the [SystemDataStore] interface to provide a means of communicating with the remote data source
 */
open class SystemRemoteDataStore(private val remote: SystemRemote): SystemDataStore {
    override suspend fun getConfigs() = remote.getConfigs()
    override suspend fun getAccount() = remote.getAccount()
    override suspend fun setAccount(accountEntity: AccountEntity) = remote.setAccount(accountEntity)
    override fun clearAccount() { throw UnsupportedOperationException() }
    override suspend fun login(email: String, password: String) = remote.login(email, password) 
}
