package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.SystemCache
import com.kg.gettransfer.data.SystemDataStore

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ConfigsEntity
import com.kg.gettransfer.data.model.EndpointEntity
import com.kg.gettransfer.data.model.LocationEntity
import com.kg.gettransfer.data.model.MobileConfigEntity

import org.koin.standalone.inject

/**
 * Implementation of the [SystemDataStore] interface to provide a means of communicating with the local data source.
 */
open class SystemDataStoreCache : SystemDataStore {
    private val cache: SystemCache by inject()

    override suspend fun getConfigs() = cache.getConfigs() //: ConfigsEntity { throw UnsupportedOperationException() }
    override suspend fun setConfigs(configsEntity: ConfigsEntity) = cache.setConfigs(configsEntity)

    override suspend fun getMobileConfigs() = cache.getMobileConfigs()
    override suspend fun setMobileConfigs(mobileConfigsEntity: MobileConfigEntity) = cache.setMobileConfigs(mobileConfigsEntity)

    override suspend fun getAccount() = cache.getAccount()
    override suspend fun setAccount(accountEntity: AccountEntity) = cache.setAccount(accountEntity)
    override suspend fun clearAccount() = cache.clearAccount()

    override suspend fun login(email: String, password: String): AccountEntity { throw UnsupportedOperationException() }
    override suspend fun accountLogin(email: String?, phone: String?, password: String): AccountEntity { throw UnsupportedOperationException() }
    override suspend fun getVerificationCode(email: String?, phone: String?): Boolean { throw UnsupportedOperationException() }
    override fun changeEndpoint(endpoint: EndpointEntity) { throw UnsupportedOperationException() }
}
