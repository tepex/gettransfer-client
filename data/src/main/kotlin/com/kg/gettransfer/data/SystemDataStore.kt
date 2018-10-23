package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ConfigsEntity

interface SystemDataStore {
    suspend fun getConfigs(): ConfigsEntity
    suspend fun setConfigs(configsEntity: ConfigsEntity)
    suspend fun getAccount(): AccountEntity
    suspend fun setAccount(accountEntity: AccountEntity)
    fun clearAccount()
    suspend fun login(email: String, password: String): AccountEntity
}
