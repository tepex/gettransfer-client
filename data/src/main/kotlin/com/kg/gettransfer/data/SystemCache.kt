package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ConfigsEntity

import org.koin.standalone.KoinComponent

interface SystemCache: KoinComponent {
    fun getConfigs(): ConfigsEntity?
    fun setConfigs(configs: ConfigsEntity)
    
    fun getAccount(): AccountEntity?
    fun setAccount(account: AccountEntity): AccountEntity
    fun clearAccount()
}
