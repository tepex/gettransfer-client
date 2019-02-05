package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.ConfigsEntity
import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.EndpointEntity
import com.kg.gettransfer.data.model.LocationEntity

import org.koin.standalone.KoinComponent

interface SystemDataStore: KoinComponent {
    suspend fun getConfigs(): ConfigsEntity?
    suspend fun setConfigs(configsEntity: ConfigsEntity)
    suspend fun getAccount(): AccountEntity?
    suspend fun setAccount(accountEntity: AccountEntity): AccountEntity
    fun clearAccount()
    suspend fun login(email: String, password: String): AccountEntity
    fun changeEndpoint(endpoint: EndpointEntity)
    suspend fun getMyLocation(): LocationEntity
}
