package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.RegistrationAccountEntity

import org.koin.core.KoinComponent

interface SessionRemote : KoinComponent {

    suspend fun authOldToken(authKey: String)

    suspend fun getAccount(): AccountEntity?

    suspend fun setAccount(accountEntity: AccountEntity): AccountEntity

    suspend fun login(email: String?, phone: String?, password: String): AccountEntity

    suspend fun signOut(): Boolean

    suspend fun register(account: RegistrationAccountEntity): AccountEntity

    suspend fun getVerificationCode(email: String?, phone: String?): Boolean

    suspend fun getConfirmationCode(email: String?, phone: String?): Boolean

    suspend fun changeContact(email: String?, phone: String?, code: String): Boolean
}
