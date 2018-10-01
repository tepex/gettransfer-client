package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.SystemRemote
import com.kg.gettransfer.data.SystemDataStore

import com.kg.gettransfer.data.model.AccountEntity

/**
 * Implementation of the [SystemDataStore] interface to provide a means of communicating with the remote data source
 */
open class SystemRemoteDataStore(private val remote: SystemRemote): SystemDataStore {
    override fun getConfigs() = remote.getConfigs()
    override fun getAccount() = remote.getAccount()
    override fun setAccount(accountEntity: AccountEntity) = remote.setAccount(accountEntity)
    override fun clearAccount() { throw UnsupportedOperationException() }
    override fun login(email: String, password: String) = remote.login(email, password) 
}
