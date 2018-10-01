package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.SystemCache
import com.kg.gettransfer.data.SystemDataStore

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ConfigsEntity

/**
 * Implementation of the [SystemDataStore] interface to provide a means of communicating with the local data source.
 */
open class SystemCacheDataStore(private val cache: SystemCache): SystemDataStore {
    override suspend fun getConfigs(): ConfigsEntity { throw UnsupportedOperationException() }
    override suspend fun getAccount() = cache.getAccount()
    override suspend fun setAccount(accountEntity: AccountEntity) = cache.setAccount(accountEntity)
    override fun clearAccount() = cache.clearAccount()
    override suspend fun login(email: String, password: String): AccountEntity { throw UnsupportedOperationException() }
}
