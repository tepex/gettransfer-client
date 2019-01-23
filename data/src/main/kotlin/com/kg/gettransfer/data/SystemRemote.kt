package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.*

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
    suspend fun getMyLocation(): LocationEntity
}
