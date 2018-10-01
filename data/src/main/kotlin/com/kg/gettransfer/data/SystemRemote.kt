package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ConfigsEntity

interface SystemRemote {
    fun getConfigs(): ConfigsEntity
    fun getAccount(): AccountEntity
    fun setAccount(accountEntity: AccountEntity)
    fun login(email: String, password: String): AccountEntity
}
