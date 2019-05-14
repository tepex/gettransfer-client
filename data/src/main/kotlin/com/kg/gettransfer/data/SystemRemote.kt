package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.ConfigsEntity
import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.EndpointEntity
import com.kg.gettransfer.data.model.MobileConfigEntity
import com.kg.gettransfer.data.model.LocationEntity

import org.koin.standalone.KoinComponent

interface SystemRemote : KoinComponent {
    suspend fun getConfigs(): ConfigsEntity
    suspend fun getAccount(): AccountEntity?
    suspend fun setAccount(accountEntity: AccountEntity): AccountEntity
    suspend fun login(email: String, password: String): AccountEntity
    suspend fun accountLogin(email: String?, phone: String?, password: String): AccountEntity
    suspend fun getVerificationCode(email: String?, phone: String?): Boolean
    fun changeEndpoint(endpoint: EndpointEntity)
    suspend fun getMobileConfigs(): MobileConfigEntity
}
