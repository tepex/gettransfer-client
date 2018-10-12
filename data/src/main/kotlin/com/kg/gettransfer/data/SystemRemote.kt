package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ConfigsEntity

interface SystemRemote {
    suspend fun getConfigs(): ConfigsEntity
    suspend fun getAccount(): AccountEntity
    suspend fun setAccount(accountEntity: AccountEntity)
    suspend fun login(email: String, password: String): AccountEntity
    fun changeEndpoint()
}
