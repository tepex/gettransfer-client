@file:Suppress("TooManyFunctions")
package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ContactEntity
import com.kg.gettransfer.data.model.RegistrationAccountEntity

import org.koin.core.KoinComponent

interface SessionDataStore : KoinComponent {

    suspend fun authOldToken(authKey: String)

    suspend fun getAccount(): AccountEntity?

    suspend fun setAccount(accountEntity: AccountEntity): AccountEntity

    suspend fun clearAccount()

    suspend fun login(contactEntity: ContactEntity<String>, password: String): AccountEntity

    suspend fun signOut(): Boolean

    suspend fun register(account: RegistrationAccountEntity): AccountEntity

    suspend fun getVerificationCode(contactEntity: ContactEntity<String>): Boolean

    suspend fun getConfirmationCode(contactEntity: ContactEntity<String>): Boolean

    suspend fun changeContact(contactEntity: ContactEntity<String>, code: String): Boolean
}
