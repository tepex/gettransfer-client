package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.AccountEntity
import com.kg.gettransfer.data.model.ConfigsEntity
import com.kg.gettransfer.data.model.MobileConfigEntity
import org.koin.core.KoinComponent

interface SessionCache : KoinComponent {

    fun getConfigs(): ConfigsEntity?

    fun setConfigs(configs: ConfigsEntity)

    fun getMobileConfigs(): MobileConfigEntity?

    fun setMobileConfigs(configs: MobileConfigEntity)

    fun getAccount(): AccountEntity?

    fun setAccount(account: AccountEntity): AccountEntity

    suspend fun clearAccount()
}
