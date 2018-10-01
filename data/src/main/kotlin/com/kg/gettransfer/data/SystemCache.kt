package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.AccountEntity

interface SystemCache {
    companion object {
        const val INVALID_TOKEN = "invalid_token"
    }

    fun getAccount(): AccountEntity
    fun setAccount(accountEntity: AccountEntity)
    fun clearAccount()
}
