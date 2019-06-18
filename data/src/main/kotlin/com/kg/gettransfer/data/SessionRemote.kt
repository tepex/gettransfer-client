package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.*

import org.koin.standalone.KoinComponent

interface SessionRemote : KoinComponent {
    suspend fun getConfigs(): ConfigsEntity
    suspend fun getAccount(): AccountEntity?
    suspend fun setAccount(accountEntity: AccountEntity): AccountEntity
    suspend fun login(email: String?, phone: String?, password: String): AccountEntity
    suspend fun register(account: RegistrationAccountEntity): AccountEntity
    suspend fun getVerificationCode(email: String?, phone: String?): Boolean
    suspend fun getCodeForChangeEmail(email: String): Boolean
    suspend fun changeEmail(email: String, code: String): Boolean
    fun changeEndpoint(endpoint: EndpointEntity)
    suspend fun getMobileConfigs(): MobileConfigEntity
}
