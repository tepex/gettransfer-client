package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ConfigsEntity
import com.kg.gettransfer.data.model.EndpointEntity

import org.koin.standalone.KoinComponent

interface SystemRemote: KoinComponent {
    suspend fun getConfigs(): ConfigsEntity
    suspend fun getAccount(): AccountEntity?
    suspend fun setAccount(accountEntity: AccountEntity): AccountEntity
    suspend fun login(email: String, password: String): AccountEntity
    suspend fun registerPushToken(provider: String, accessToken: String)
    suspend fun unregisterPushToken(accessToken: String)
    fun changeEndpoint(endpoint: EndpointEntity)
}
