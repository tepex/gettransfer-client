package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ConfigsEntity

interface SystemDataStore {
    fun getConfigs(): ConfigsEntity
    fun getAccount(): AccountEntity
    fun setAccount(accountEntity: AccountEntity)
    fun clearAccount()
    fun login(email: String, password: String): AccountEntity
}
