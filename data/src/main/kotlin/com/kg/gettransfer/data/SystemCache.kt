package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ConfigsEntity

interface SystemCache {
    fun getConfigs(): ConfigsEntity?
    fun setConfigs(configs: ConfigsEntity)
    
    fun getAccount(): AccountEntity?
    fun setAccount(account: AccountEntity)
    fun clearAccount()
}
