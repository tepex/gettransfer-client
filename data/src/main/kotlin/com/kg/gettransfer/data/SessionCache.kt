package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.AccountEntity

import org.koin.core.KoinComponent

interface SessionCache : KoinComponent {

    suspend fun getAccount(): AccountEntity?

    suspend fun setAccount(account: AccountEntity): AccountEntity

    suspend fun clearAccount()
}
