package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.AccountEntity

import org.koin.core.KoinComponent

interface SessionCache : KoinComponent {

    fun getAccount(): AccountEntity?

    fun setAccount(account: AccountEntity): AccountEntity

    suspend fun clearAccount()
}
