package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.SessionRemote
import com.kg.gettransfer.data.SessionDataStore

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.RegistrationAccountEntity

import org.koin.core.inject

/**
 * Implementation of the [SessionDataStore] interface to provide a means of communicating with the remote data source
 */
open class SessionDataStoreRemote : SessionDataStore {

    private val remote: SessionRemote by inject()

    override suspend fun updateOldToken() = remote.updateOldToken()

    override suspend fun getAccount() = remote.getAccount()

    override suspend fun setAccount(accountEntity: AccountEntity) = remote.setAccount(accountEntity)

    override suspend fun login(email: String?, phone: String?, password: String) = remote.login(email, phone, password)

    override suspend fun register(account: RegistrationAccountEntity) = remote.register(account)

    override suspend fun getVerificationCode(email: String?, phone: String?) = remote.getVerificationCode(email, phone)

    override suspend fun clearAccount() {
        throw UnsupportedOperationException()
    }

    override suspend fun getConfirmationCode(email: String?, phone: String?) = remote.getConfirmationCode(email, phone)

    override suspend fun changeEmail(email: String, code: String) = remote.changeEmail(email, code)
}
