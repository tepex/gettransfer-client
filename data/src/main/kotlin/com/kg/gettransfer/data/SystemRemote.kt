package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ConfigsEntity
import com.kg.gettransfer.data.model.EndpointEntity
import com.kg.gettransfer.data.model.MobileConfigEntity

import org.koin.standalone.KoinComponent

interface SystemRemote : KoinComponent {
    suspend fun getConfigs(): ConfigsEntity
    suspend fun getAccount(): AccountEntity?
    suspend fun setAccount(accountEntity: AccountEntity): AccountEntity
    suspend fun login(email: String, password: String): AccountEntity
    suspend fun registerPushToken(provider: String, token: String)
    suspend fun unregisterPushToken(token: String)
    fun changeEndpoint(endpoint: EndpointEntity)
    suspend fun getMobileConfig(): MobileConfigEntity
}
