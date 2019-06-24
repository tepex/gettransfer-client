@file:Suppress("TooManyFunctions")
package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.SessionRemote
import com.kg.gettransfer.data.SessionDataStore
import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ConfigsEntity
import com.kg.gettransfer.data.model.EndpointEntity
import com.kg.gettransfer.data.model.MobileConfigEntity
import com.kg.gettransfer.data.model.RegistrationAccountEntity

import org.koin.standalone.inject

/**
 * Implementation of the [SessionDataStore] interface to provide a means of communicating with the remote data source
 */
open class SessionDataStoreRemote : SessionDataStore {

    private val remote: SessionRemote by inject()

    override suspend fun getConfigs() = remote.getConfigs()

    override suspend fun setConfigs(configsEntity: ConfigsEntity) {
        throw UnsupportedOperationException()
    }

    override suspend fun getMobileConfigs() = remote.getMobileConfigs()

    override suspend fun setMobileConfigs(mobileConfigsEntity: MobileConfigEntity) {
        throw UnsupportedOperationException()
    }

    override suspend fun getAccount() = remote.getAccount()

    override suspend fun setAccount(accountEntity: AccountEntity) = remote.setAccount(accountEntity)

    override suspend fun login(email: String?, phone: String?, password: String) = remote.login(email, phone, password)

    override suspend fun register(account: RegistrationAccountEntity) = remote.register(account)

    override suspend fun getVerificationCode(email: String?, phone: String?) = remote.getVerificationCode(email, phone)

    override suspend fun clearAccount() {
        throw UnsupportedOperationException()
    }

    override fun changeEndpoint(endpoint: EndpointEntity) = remote.changeEndpoint(endpoint)

    override suspend fun getCodeForChangeEmail(email: String) = remote.getCodeForChangeEmail(email)

    override suspend fun changeEmail(email: String, code: String) = remote.changeEmail(email, code)
}
