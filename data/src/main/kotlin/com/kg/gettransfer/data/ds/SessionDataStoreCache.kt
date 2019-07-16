package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.SessionCache
import com.kg.gettransfer.data.SessionDataStore

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.RegistrationAccountEntity

import org.koin.core.inject

/**
 * Implementation of the [SessionDataStore] interface to provide a means of communicating with the local data source.
 */
open class SessionDataStoreCache : SessionDataStore {

    private val cache: SessionCache by inject()

    override suspend fun getAccount() = cache.getAccount()

    override suspend fun setAccount(accountEntity: AccountEntity) = cache.setAccount(accountEntity)

    override suspend fun clearAccount() = cache.clearAccount()

    override suspend fun login(email: String?, phone: String?, password: String): AccountEntity {
        throw UnsupportedOperationException()
    }

    override suspend fun register(account: RegistrationAccountEntity): AccountEntity {
        throw UnsupportedOperationException()
    }

    override suspend fun getVerificationCode(email: String?, phone: String?): Boolean {
        throw UnsupportedOperationException()
    }

    override suspend fun getCodeForChangeEmail(email: String): Boolean {
        throw UnsupportedOperationException()
    }

    override suspend fun changeEmail(email: String, code: String): Boolean {
        throw UnsupportedOperationException()
    }
}
