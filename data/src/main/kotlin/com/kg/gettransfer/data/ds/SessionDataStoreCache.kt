package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.SessionCache
import com.kg.gettransfer.data.SessionDataStore

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ConfigsEntity
import com.kg.gettransfer.data.model.EndpointEntity
import com.kg.gettransfer.data.model.MobileConfigEntity

import org.koin.standalone.inject

/**
 * Implementation of the [SessionDataStore] interface to provide a means of communicating with the local data source.
 */
open class SessionDataStoreCache : SessionDataStore {

    private val cache: SessionCache by inject()

    override suspend fun getConfigs() = cache.getConfigs() //: ConfigsEntity { throw UnsupportedOperationException() }
    override suspend fun setConfigs(configsEntity: ConfigsEntity) = cache.setConfigs(configsEntity)

    override suspend fun getMobileConfigs() = cache.getMobileConfigs()
    override suspend fun setMobileConfigs(mobileConfigsEntity: MobileConfigEntity) =
        cache.setMobileConfigs(mobileConfigsEntity)

    override suspend fun getAccount() = cache.getAccount()
    override suspend fun setAccount(accountEntity: AccountEntity) = cache.setAccount(accountEntity)
    override suspend fun clearAccount() = cache.clearAccount()

    override suspend fun login(email: String?, phone: String?, password: String): AccountEntity {
        throw UnsupportedOperationException()
    }

    override suspend fun register(name: String, phone: String, email: String, termsAccepted: Boolean): AccountEntity {
        throw UnsupportedOperationException()
    }

    override suspend fun getVerificationCode(email: String?, phone: String?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun changeEndpoint(endpoint: EndpointEntity) {
        throw UnsupportedOperationException()
    }
}
