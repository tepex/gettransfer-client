package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.SessionRemote
import com.kg.gettransfer.data.SessionDataStore

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ContactEntity
import com.kg.gettransfer.data.model.RegistrationAccountEntity

import org.koin.core.inject

/**
 * Implementation of the [SessionDataStore] interface to provide a means of communicating with the remote data source
 */
open class SessionDataStoreRemote : SessionDataStore {

    private val remote: SessionRemote by inject()

    override suspend fun authOldToken(authKey: String) = remote.authOldToken(authKey)

    override suspend fun getAccount() = remote.getAccount()

    override suspend fun setAccount(accountEntity: AccountEntity) = remote.setAccount(accountEntity)

    override suspend fun login(contactEntity: ContactEntity<String>, password: String) =
        remote.login(contactEntity, password)

    override suspend fun signOut() = remote.signOut()

    override suspend fun register(account: RegistrationAccountEntity) = remote.register(account)

    override suspend fun getVerificationCode(contactEntity: ContactEntity<String>) =
        remote.getVerificationCode(contactEntity)

    override suspend fun clearAccount() {
        throw UnsupportedOperationException()
    }

    override suspend fun getConfirmationCode(contactEntity: ContactEntity<String>) =
        remote.getConfirmationCode(contactEntity)

    override suspend fun changeContact(contactEntity: ContactEntity<String>, code: String) =
        remote.changeContact(contactEntity, code)
}
